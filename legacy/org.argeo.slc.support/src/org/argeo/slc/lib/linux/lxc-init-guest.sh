echo demo | passwd root --stdin

#Fix root login on console
echo "pts/0" >>/etc/securetty
sed -i s/"session    required     pam_selinux.so close"/"#session    required     pam_selinux.so close"/g /etc/pam.d/login
sed -i s/"session    required     pam_selinux.so open"/"#session    required     pam_selinux.so open"/g /etc/pam.d/login
sed -i s/"session    required     pam_loginuid.so"/"#session    required     pam_loginuid.so"/g /etc/pam.d/login

#Configuring basic networking
cat > /etc/sysconfig/network << EOF
NETWORKING=yes
HOSTNAME=demo
EOF
cat > /etc/sysconfig/network-scripts/ifcfg-eth0 << EOF
DEVICE=eth0
BOOTPROTO=dhcp
ONBOOT=yes
EOF

#Enabling sshd
chkconfig sshd on

# Fixing root login for sshd
sed -i s/"session    required     pam_selinux.so close"/"#session    required     pam_selinux.so close"/g /etc/pam.d/sshd
sed -i s/"session    required     pam_loginuid.so"/"#session    required     pam_loginuid.so"/g /etc/pam.d/sshd
sed -i s/"session    required     pam_selinux.so open env_params"/"#session    required     pam_selinux.so open env_params"/g /etc/pam.d/sshd

# Leaving the chroot'ed filesystem
exit