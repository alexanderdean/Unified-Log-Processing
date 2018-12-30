#!/bin/bash
set -e

vagrant_dir=/vagrant/vagrant
bashrc=/home/vagrant/.bashrc

echo "========================================"
echo "INSTALLING PERU AND ANSIBLE DEPENDENCIES"
echo "----------------------------------------"
sudo add-apt-repository -y ppa:jonathonf/python-3.6
apt-get update
sudo apt-get install -y build-essential \
  libpq-dev \
  libssl-dev \
  openssl \
  libffi-dev \
  zlib1g-dev \
  python-apt
sudo apt-get install -y python3.6 \
  python3.6-dev
curl https://bootstrap.pypa.io/get-pip.py | sudo -H python3.6
sudo pip3 install --upgrade virtualenvwrapper --ignore-installed six
sudo ln -s /usr/bin/python3.6 /usr/local/bin/python3
apt-get install -y language-pack-en git unzip libyaml-dev python-yaml python-paramiko python-jinja2

echo "==============="
echo "INSTALLING PERU"
echo "---------------"
sudo pip3 install peru

echo "======================================="
echo "CLONING ANSIBLE AND PLAYBOOKS WITH PERU"
echo "---------------------------------------"
cd ${vagrant_dir} && peru sync -v
echo "... done"

env_setup=${vagrant_dir}/ansible/hacking/env-setup
hosts=${vagrant_dir}/ansible.hosts

echo "==================="
echo "CONFIGURING ANSIBLE"
echo "-------------------"
touch ${bashrc}
echo "source ${env_setup}" >> ${bashrc}
echo "export ANSIBLE_HOSTS=${hosts}" >> ${bashrc}
echo "... done"

echo "=========================================="
echo "RUNNING PLAYBOOKS WITH ANSIBLE*"
echo "* no output while each playbook is running"
echo "------------------------------------------"
while read pb; do
    su - -c "source ${env_setup} && ${vagrant_dir}/ansible/bin/ansible-playbook ${vagrant_dir}/${pb} --connection=local --inventory-file=${hosts}" vagrant
done <${vagrant_dir}/up.playbooks

guidance=${vagrant_dir}/up.guidance

if [ -f ${guidance} ]; then
    echo "==========="
    echo "PLEASE READ"
    echo "-----------"
    cat $guidance
fi
