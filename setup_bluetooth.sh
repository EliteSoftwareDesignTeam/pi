wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.48.tar.xz
rm bluez-5.37.tar.xz
tar xvf bluez-5.48.tar.xz
cd bluez-5.48/
sudo apt-get update
sudo apt-get install -y libusb-dev libdbus-1-dev libglib2.0-dev libudev-dev libical-dev libreadline-dev
./configure
make
make install
sudo make install
sudo systemctl enable bluetooth
systemctl daemon-reload
echo "Added -C to the line containing bluetoothhd"
read -p "Press enter when ready" null
sudo nano /lib/systemd/system/bluetooth.service
pip install pyobjc
pip install pybluez
sudo apt-get install libbluetooth-dev
sudo pip install pybluez