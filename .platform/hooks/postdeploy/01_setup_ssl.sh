#!/bin/bash

# Ensure script runs as root
if [[ $EUID -ne 0 ]]; then
    echo "This script must be run as root"
    exit 1
fi

# Update package lists
sudo dnf update -y

# Install Certbot if not installed
if ! command -v certbot &> /dev/null; then
    sudo dnf install -y snapd
    sudo systemctl enable --now snapd.socket
    sudo ln -s /var/lib/snapd/snap /snap
    sudo snap install core
    sudo snap refresh core
    sudo snap install --classic certbot
    sudo ln -s /snap/bin/certbot /usr/bin/certbot
fi

# Stop Nginx to free port 80 for Certbot verification
sudo systemctl stop nginx

# Request Let's Encrypt certificate (auto-configures Nginx)
sudo certbot --nginx --noninteractive --agree-tos \
  --email your-email@example.com \
  -d booking-app.us-east-1.elasticbeanstalk.com

# Ensure correct permissions for SSL certs
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/fullchain.pem
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/privkey.pem

# Enable auto-renewal using systemd
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
sudo systemctl daemon-reload
sudo systemctl enable --now certbot-renew.timer

# Restart Nginx
sudo systemctl start nginx
