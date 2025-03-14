#!/bin/bash

# Ensure Nginx config is in the right location
cp -f /var/app/current/.platform/nginx/conf.d/https_redirect.conf /var/proxy/staging/nginx/conf.d/ || echo "Nginx Config Not Found"

# Restart Nginx to apply changes
systemctl restart nginx
