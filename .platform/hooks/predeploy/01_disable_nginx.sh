#!/bin/bash
set -e

echo "DISABLE_NGINX=1" | sudo tee /etc/environment
sudo systemctl restart nginx
