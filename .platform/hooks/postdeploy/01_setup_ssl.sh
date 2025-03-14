#!/bin/bash

# Install certbot if not already installed
if ! command -v certbot &> /dev/null
then
    sudo yum install -y certbot
fi

# Stop Nginx to free port 80 for Certbot verification
sudo systemctl stop nginx

# Obtain/Renew Let's Encrypt Certificate
sudo certbot certonly --standalone --noninteractive --agree-tos \
  --email your-email@example.com \
  -d booking-app.us-east-1.elasticbeanstalk.com

# Ensure correct permissions for the SSL cert files
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/fullchain.pem
sudo chmod 644 /etc/letsencrypt/live/booking-app.us-east-1.elasticbeanstalk.com/privkey.pem

# Restart Nginx with the new certificate
sudo systemctl start nginx
