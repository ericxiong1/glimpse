# Glimpse User Guide
# **Authors**
Shane Kunjachen\
Lukas Bonkowski\
Eric Xiong\
## **System Requirements**

Glimpse can be run on any Android device, running Android 13 or greater, that has access to a camera, microphone, and the device’s location. 

## **Application Prerequisites**

To unlock the full functionality of Glimpse, permission to access the location, microphone, camera, notifications and write to system settings will be required. You will be prompted upon the initial startup of the application to grant this access. Please ensure Glimpse has been granted complete access.

Glimpse requires a stable internet connection. Please ensure you are connected to a Wi-Fi or mobile network.

To control the application with a wireless pointer device, a Bluetooth connection is required. In the mobile device’s Bluetooth settings, connect the pointer device. Once connected, you should be able to control the application with the connected peripheral.

## **Installing Glimpse**

To install Glimpse, follow the steps described below:

1. Download the Glimpse APK file from the shared drive (app-debug.apk) and download the file onto the device
2. Alternatively, create a gradle.properties file by copying sample-grade.properties and filling out the API keys  
3. Then, using android studio, run the project

## **Initial Setup**

1. Start the Glimpse application.  
2. When the application starts, you will be prompted to grant permission to access the device’s precise location, microphone, and camera. Please ensure access is granted to these sensors while using the application. If access to any sensor is not granted, the application will not function.  
3. A pop up should take you to the phone’s system settings. Grant Glimpse the Notification Access setting, then hit back and grant glimpse Change System Settings. Hit back again, or return to the app and you should see the Glimpse home screen

## **Accessing the HUD**

1. From the Glimpse home screen, click the “HUD” button.  
2. The HUD screen will appear in landscape orientation.

## **Exiting the HUD**

1. To exit the HUD, press and hold any location on the HUD screen. If using a pointer device, click and hold the primary button on the control device to exit the HUD, otherwise hold down on the center of the screen.

## **Controlling the HUD**

You can control the HUD , using touch, a Bluetooth-connected device (**FR6 \- Control.Widget**), or voice controls (**FR7 \- Control.Voice**). However touch is only intended for exiting the hud. 

When inside the HUD, a menu bar will appear at the bottom of the display, containing toggles for the various widgets that are available. After 10 seconds of inactivity, the menu bar will disappear. If touch or pointer control is used for the HUD, the menu bar can be shown by clicking anywhere on the screen. To access the menu bar, using voice controls, the command “Glimpse” can be stated. 

To hide the HUD display, the voice command, *“Glimpse, off”* can be used or a double click of the screen (**FR25 \- Off.Widget**). To turn the HUD back on say “*Glimpse, on*” or double click again 

## **Accessing Widgets**

To access the various widgets inside the HUD, you can click the desired widget from the menu bar to activate it.

To access a widget inside the HUD, using a pointer device, you can use the scroll wheel of the device to cycle through all available widgets. If the menu bar is hidden, using the scroll wheel will cause the bar to reappear.

To access a widget inside the HUD, using voice controls, commands in the following format can be stated: *“Glimpse, start \_\_\_\_\_\_\_”*. 

## **Facial Recognition**

### Adding New Faces (**FR20 \- Recognition.Upload**)

To use the facial recognition functionality in Glimpse, you will need to add new faces to the application, along with their associated name and details. To do this:

1. Launch the Glimpse application.  
2. From the main menu, select **Manage Facial Recognition**.  
3. Select **Upload Face**.  
4. Enter the new individual’s name and any associated information, in their respective text fields.  
5. Upload or capture an image of the individual’s face, using the buttons on the screen.  
   1. Ensure that only the desired individual’s face is in the image. If there is more than one face detected in the image, the system will notify you and prevent you from saving that image. Similarly, if the system does not detect any face in the image, it will notify you and prevent you from saving the image.  
6. Once all fields in the form are completed, select **Submit**.  
7. The new face has now been stored inside the application.

### Viewing Saved Faces

To view all faces currently stored inside Glimpse:

1. Launch the Glimpse application.  
2. From the main menu, select **Manage Facial Recognition**.  
3. Select **View Existing Faces**.  
4. All currently saved faces will be displayed.  
   1. To delete an entry, click the X to the right of the desired entry.

### Using Facial Recognition (**FR21 \- Recognition.Widget**)

To use the facial recognition functionality inside Glimpse:

1. From the HUD, navigate to the Face widget.  
2. Point the device towards an individual, such that the rear camera can see the face.  
3. If the system recognizes the face as one saved in the application, their name and associated information will be displayed (**FR22 \- Recognition.View**). If not, an appropriate message will be displayed. 

## **Weather (FR10 \- Weather.Widget)**

Launching the weather widget from the HUD will display the weather from the last pinged location of the device. Periodically, the location of the device will be pinged and used in an OpenWeatherMap API call to keep the weather up-to-date.

## **Navigation (FR16 \- Get.Destination)**

To use the navigation function in Glimpse, a destination must be entered before launching the HUD. To do this:

1. Launch the Glimpse application.  
2. From the main menu, select **Destination Input**.  
3. In the text box, enter the desired destination. Suggested locations will appear, as text is entered.  
4. Select the desired destination from the list.  
5. Click **Confirm Destination**.  
6. The HUD can now be launched and when the navigation widget is accessed, directions to the selected destination will be displayed. (**FR17 \- Get.Next.Turn**)  
7. Follow the instructions on the HUD to arrive at the destination, if a wrong turn is taken, an update will be provided (**FR18 \- Update.Route**)

## **Notifications (FR8 \- Notification.Visible)**

When the device receives a notification that would typically appear on the screen, Glimpse will intercept it and produce a HUD friendly notification. This notification will appear for 7 seconds, but can also be dismissed by sliding it down or pressing the X.

The recommended test for this functionality is to install Discord on the device and send a message to the account on the device.

## **Enabling/Disabling Do Not Disturb (FR9 \- Notification.DoNotDisturb)**

Glimpse allows you to mute all incoming notifications by entering a Do Not Disturb mode. To do this:

1. From the HUD display, navigate to the Settings widget.  
2. Use the Do Not Disturb toggle to control the status of notifications within the HUD. 

## **Adjusting Volume/Brightness (FR5 \- Change.Brightness, FR24 \- Change.Volume)**

Glimpse allows you to control the brightness of the HUD, as well as the volume of any audio that’s played while inside the HUD. To do this:

1. From the HUD, navigate to the Settings widget.   
2. Use the volume and brightness slider controls to adjust these settings, as desired.

Alternatively, these settings can be adjusted, using voice controls, using the command, *“Glimpse, set \<brightness|volume\> to \<0-100\>”*.

## **Scanning Barcodes (FR13 \- Barcode.Scanning, FR15 \- Barcode.Priority)**

Glimpse allows you to scan barcodes and retrieve associated information. Currently, it is capable of identifying books from barcodes. This functionality does not require any additional setup.

1. From the HUD, navigate to the Barcode widget.  
2. Point the device towards the barcode, such that the rear camera is able to see the barcode.  
3. If the barcode is of a book, the name and author of the book will be displayed. (**FR14 \- Barcode.Lookup**)
4. If the barcode is of a UPC product, the name of the product will be displayed if in UPC item db. (**FR14 \- Barcode.Lookup**)
5. If the barcode is none of the above or not found, the serial number or other information from MLkit will be displayed 

## **Chat (FR19 \- Chat.Widget)**  
The chat widget allows you to talk directly with Google’s LLM Gemini.   
Pointer control: 

* Left click on the center of the screen to begin recording your voice  
* Right click to send a picture of what the camera currently views to Gemini for description

Voice control:

* Use “**Gemini, \<ask gemini a question\>**” to control completely by voice  
  * if **\<ask gemini a question**\> contains one of "photo", "image", "picture", "capture", "camera" a image will automatically be captured and attached to your query

## **FR23 \- Chat.TextToSpeech**  
Results will be read aloud by a TTS engine whose volume is controlled by the volume setting.  
Additionally a text box at the top will also display the gemini response. Using a scroll wheel on a pointer device the results can be viewed if they exceed 2 lines

## **Compass (FR11 \- Compass.Widget)**  
Launching the compass will give the current heading (N, NE, N …) the rear camera of the phone is facing.  
The alternate orientation (**FR12 \- Compass.Orientation**), with the rear camera facing the sky, if the rear is pointing to the sky the compass direction is adjusted to be that of the top edge of the phone in a landscape orientation with the top (charging port) to the right.  
 

## **Customization**

Glimpse allows various changes to the HUD to be made to configure the display:

1. From the Home Screen, click the Edit icon on the bottom nav bar.  
   

### Manipulating the HUD (**FR1 \- Manipulate.HUD)**:

2. From the Edit Screen, click on the Projection Options Icon (1st item on the screen).  
3. You will see a preview at the top of the screen. To manipulate the scale of the HUD, change the Scale X/Y sliders from 0.50-1.00.  
4. To manipulate the mirroring of the HUD, slide Mirror X/Y on/off.  
5. To translate the HUD in the X and Y directions, change the Offset X/Y sliders from \-200 to 200 dp.  
6. To rotate the HUD in the X, Y, and Z directions, change the Rotation X/Y/Z sliders from \-180 to 180 degrees.  
7. If your camera feed is rotated by a lens/mirror, you may slide the Rotate Camera Feed slider On/Off.  
8. To save pending changes, press the “Save” button located at the bottom of the screen.  
   

### Changing the Font/Colors (**FR2 \- Change.Font/FR3 \- Change.Colors)**

1. From the Edit Screen, click on the Customization Icon (2nd item on the screen).  
2. You will see a preview at the top of the screen. If you would like to change the Background Color of the HUD (default Black), change the Red, Green, and Blue sliders from 0 to 255\.  
3. If you would like to change the Foreground Color of the HUD (text color, menu outline color), change the Red, Green, and Blue sliders from 0 to 255\.  
4. If you would like to change the HUD font, click on the Font Selection dropdown, and select between System Default, Sans Serif, Serif, and Monospace.  
5. To save pending changes, press the “Save” button located at the bottom of the screen.  
6. To save pending changes, press the “Save” button located at the bottom of the screen.

### Changing Widget Positions (Single Widget Mode)  (**FR4 \- Change.Positions)**

1. From the Edit Screen, click on the Edit HUD Icon (3rd item on the screen).  
2. You will see a preview at the top of the screen. By default, the HUD will be in Single Widget Mode.Under Customize Widget Menu, there will be a list of menu items that will be placed on the menu bar on the HUD.   
3. Using the hamburger on the right side of each item, they can be reordered top-down.  
4. If you would like to hide a specific item, you may uncheck a widget from appearing (and being able to be accessed) on the HUD.  
5. If you would like to change the widget placement on the HUD screen in Single Widget Mode, you may reassign the location (Top, Middle Left, Middle Right, Bottom) using the dropdown menu under Widget Assignments.\\  
6. To save pending changes, press the “Save” button located at the bottom of the screen.

### Changing Widget Positions (Multi-Widget Mode)  (**FR4 \- Change.Positions)**

1. Under the HUD preview, you can turn on Multi-Widget mode by switching off Single Widget Mode.  
2. Similar to Single Widget Mode, using the hamburger on the right side of each item, they can be reordered top-down. Note that only camera items are shown on the menu bar in this mode.  
3. If you would like to hide a specific item, you may uncheck a widget from appearing (and being able to be accessed) on the HUD.  
4. Under Widget Assignments, you may assign specific widgets to specific areas on the HUD display. Note that “Camera” refers to widgets that use the camera. To select what widget is currently using the camera, you can select the respective widget using the menu bar.  
5. To save pending changes, press the “Save” button located at the bottom of the screen.

Glimpse also allows you to reset all customization changes to their system defaults. At the bottom above “Save”, there is a “Reset to Defaults” button. Hitting this will reset all pending and current settings back to their original state.
