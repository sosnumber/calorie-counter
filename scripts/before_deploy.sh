#!/bin/bash
if [ -d /home/ec2-user/calorie-counter ]; then
  sudo rm -rf /home/ec2-user/calorie-counter
fi
sudo mkdir -vp /home/ec2-user/calorie-counter