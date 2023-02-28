#!/bin/bash
sudo rsync --delete-before --verbose --archive /home/ec2-user/calorie-counter / > /var/log/deploy.log