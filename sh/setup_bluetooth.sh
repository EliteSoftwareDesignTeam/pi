set -o xtrace -e
echo "Installing library things that are supposed to be useful"
sudo apt-get update
sudo apt-get install -y libusb-dev libdbus-1-dev libglib2.0-dev libudev-dev libical-dev libreadline-dev

echo "Downloading and installing bluez v5.48"
wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.48.tar.xz
tar xvf bluez-5.48.tar.xz
cd bluez-5.48/
./configure
make
sudo make install

echo "Enabling bluetooth service"
sudo systemctl enable bluetooth
systemctl daemon-reload
read -p "Add -C to the line containing bluetoothhd. Press enter when ready" null
sudo nano /lib/systemd/system/bluetooth.service

echo "Installing pyobjc and pybluez"
pip install pyobjc
sudo apt-get install libbluetooth-dev
sudo pip install pybluez