# Enable SSH on Ubuntu MATE

```
sudo apt-get update
sudo apt-get install openssh-server
sudo ufw allow 22
sudo /etc/init.d/ssh start
```

Enable ssh server on boot

```
sudo systemctl enable ssh.service
```
