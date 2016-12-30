-------------------------------------------------
Johannes Wagner, <wagner@hcm-lab.de>, 23.08.2016
-------------------------------------------------

Instructions to set up ARIA's virtual machine (VM) for ASR.

1. Download and install VMware Workstation Player 12 for Windows 64-bit

https://my.vmware.com/en/web/vmware/free#desktop_end_user_computing/vmware_workstation_player/12_0

2. Download and unzip the VM to your hard drive (requires ~ 30 GB disk space and may occupy up to 6 GB RAM)

url:              https://hcm-lab.de/cloud/index.php/s/r6x9ha8XD3YJNzn
password:  Wonderland2016

3. At the first start of Workstation Player 12 select 'Open a Virtual Machine' and choose 'Ubuntu 64-bit.vmx' from the VM folder (select 'I copied it')

4. Press 'Play virtual machine'

5. In your home directory you'll find two scripts 'GERMAN.sh' and 'ENGLISH.sh' to run the ASR for different languages (in different terminal windows)

6. (Probably set the Keyboard settings to English)

7. Run the english asr by opening a terminal and typing './ENGLISH.sh'

-------------------------------------------------
Credentials
-------------------------------------------------

username: alice
password: Wonderland2016

-------------------------------------------------
Network
-------------------------------------------------

To access the network settings choose 'Player->Manage->Virtual Machine Settings' and select'Network Adapter':

- Choose 'NAT' if you need to access the internet 
- Choose 'Host-only' while running the ASR (allows the host to access the VM)

-------------------------------------------------
Prepare host pipeline
-------------------------------------------------

1. Open a terminal and run 'ifconfig' to receive the IP address of the VM (should be in the format 192.168.*.*)

2. Use this address in the configuration dialog

