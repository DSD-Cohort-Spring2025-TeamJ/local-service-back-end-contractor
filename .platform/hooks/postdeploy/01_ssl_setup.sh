#!/bin/bash

cp -f /var/app/current/.platform/nginx/conf.d/https_redirect.conf /etc/nginx/conf.d/
systemctl restart nginx

chmod +x .platform/hooks/postdeploy/01_ssl_setup.sh

echo "Installing Certbot..."
sudo yum install -y certbot

echo "Stopping NGINX..."
sudo systemctl stop nginx

echo "Requesting SSL certificate..."
sudo certbot certonly --standalone -d booking-app.us-east-1.elasticbeanstalk.com --non-interactive --agree-tos -m your-email@example.com

echo "Restarting NGINX..."
sudo systemctl start nginx

echo "Adding cron job for automatic SSL renewal..."
# Remove any existing certbot cron jobs to avoid duplicates
sudo crontab -l | grep -v 'certbot renew' | sudo crontab -

# Add a new cron job to renew SSL daily at 2 AM
echo "0 2 * * * certbot renew --quiet --post-hook 'sudo systemctl restart nginx'" | sudo crontab -

echo "SSL setup and auto-renewal configured!"

