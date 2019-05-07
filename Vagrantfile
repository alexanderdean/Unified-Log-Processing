Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/xenial64"
  config.vm.hostname = "ulp"
  config.ssh.forward_agent = true

  # Forward guest ports for YARN
  config.vm.network "forwarded_port", guest: 8088, host: 8188
  config.vm.network "forwarded_port", guest: 8042, host: 8142

  config.vm.provider :virtualbox do |vb|
    vb.name = Dir.pwd().split("/")[-1] + "-" + Time.now.to_f.to_i.to_s
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    vb.customize [ "guestproperty", "set", :id, "--timesync-threshold", 10000 ]
    # Scala is memory-hungry
    vb.memory = 3200
  end

  config.vm.provision :shell do |sh|
    sh.path = "vagrant/up.bash"
  end
  
  config.vm.provision "shell", path: "java_setup.sh"

end
