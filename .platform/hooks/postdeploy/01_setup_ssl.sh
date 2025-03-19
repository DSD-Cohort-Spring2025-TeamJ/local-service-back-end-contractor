#!/bin/bash

# Exit immediately if a command fails
set -e

# Update package lists
sudo dnf update -y

# Install Certbot via Snap if not already installed
if ! command -v certbot &> /dev/null; then
    echo "Installing Snap & Certbot..."
    sudo dnf install -y snapd
    sudo systemctl enable --now snapd.socket
    sudo ln -s /var/lib/snapd/snap /snap
    sudo snap install core
    sudo snap refresh core
    sudo snap install --classic certbot
    sudo ln -s /snap/bin/certbot /usr/bin/certbot
else
    echo "Certbot is already installed."
fi

# Stop Nginx to free port 80 for Certbot verification
echo "Stopping Nginx..."
sudo systemctl stop nginx

# Request Let's Encrypt certificate (auto-configures Nginx)
echo "Requesting SSL certificate from Let's Encrypt..."
sudo certbot --nginx --noninteractive --agree-tos \
  --email joshuaow@gmail.com \
  -d booking-app.us-east-1.elasticbeanstalk.com

# Ensure correct permissions for SSL certs
echo "Setting correct permissions for SSL certificates..."
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/fullchain.pem
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/privkey.pem

# Set up auto-renewal systemd timer for Certbot
echo "Configuring auto-renewal..."
cat << EOF | sudo tee /etc/systemd/system/certbot-renew.service > /dev/null
[Unit]
Description=Renew Let's Encrypt certificates
Wants=network-online.target
After=network-online.target

[Service]
Type=oneshot
ExecStart=/usr/bin/certbot renew --quiet
EOF

cat << EOF | sudo tee /etc/systemd/system/certbot-renew.timer > /dev/null
[Unit]
Description=Run certbot-renew twice daily
Wants=network-online.target
After=network-online.target

[Timer]
OnCalendar=*-*-* 00,12:00:00
Persistent=true

[Install]
WantedBy=timers.target
EOF

# Reload systemd and enable timer
echo "Enabling systemd renewal timer..."
sudo systemctl daemon-reload
sudo systemctl enable --now certbot-renew.timer

# Restart Nginx
echo "Restarting Nginx..."
sudo systemctl start nginx

echo "SSL setup completed successfully!"

# Copy custom nginx config from app source bundle to system
echo "Copying custom nginx configuration..."
sudo cp -f /var/app/staging/.platform/nginx/nginx.conf /etc/nginx/nginx.conf
sudo cp -f /var/app/staging/.platform/nginx/conf.d/* /etc/nginx/conf.d/

# Test and restart nginx with new config
echo "Testing new Nginx configuration..."
sudo nginx -t && sudo systemctl restart nginx

echo "Custom nginx configuration applied!"
