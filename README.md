Unified Log Processing
======================

Supporting material (code, schemas etc) for **[Unified Log Processing] [ulp]**, authored by Alexander Dean and published by Manning Publications.

## Quickstart

Assuming git, **[Vagrant] [vagrant-install]** and **[VirtualBox] [virtualbox-install]** installed:

```bash
 host$ git clone https://github.com/alexanderdean/Unified-Log-Processing.git
 host$ cd snowplow
 host$ vagrant up && vagrant ssh
guest$ cd /vagrant/ch02/2.1/hellocalculator
guest$ gradle test
```

[ulp]: http://manning.com/dean/

[vagrant-install]: http://docs.vagrantup.com/v2/installation/index.html
[virtualbox-install]: https://www.virtualbox.org/wiki/Downloads
