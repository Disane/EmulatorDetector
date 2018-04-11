# DEMO Script: Evading Emulator Detection using Frida
# Platform: Android AOSP ver. 4.4. ARM - SELinux Permissive
# Requirements: EmulatorDetector
#  Called by InstrumentEmuDetectorApp.py
# Run: python InstrumentEmuDetectorApp.py
import frida
import sys
import time

__author__ = "elias.t"
__company__ = "IKARUS Security Software GmbH."
__Country__ = "Austria"

package_name = "emulatordetector.java.ikarus.at.myapplication"
wait_seconds = 2
script = "pyFED.js"


def on_message(message, data):
    if message['type'] == 'send':
        print("[*] {0}".format(message['payload']))
    else:
        print(message)


def load_script(script_name):
    '''
    We assume the script is in the working directory
    [TODO]: implement os.path to be able to pass an absolute path to any instrument script
    '''
    hook_code = ""
    with open(script_name, "r") as f:
        hook_code = f.read()
    return hook_code


# process = frida.get_usb_device().attach(package_name)
print("Acquiring USB device...")
device = frida.get_usb_device()
print("Spawning " + package_name)
process = device.spawn([package_name])
print("Attaching to PID: " + str(process))
session = device.attach(process)
print("Loading instrument script: " + script)
script = session.create_script(load_script(script))
script.on('message', on_message)
script.load()
print("Sleeping " + str(wait_seconds) + " seconds")
time.sleep(wait_seconds)
print("Resuming package_name @PID: " + str(process))
device.resume(process)
print("Press CTRL+C to exit...")
sys.stdin.read()
