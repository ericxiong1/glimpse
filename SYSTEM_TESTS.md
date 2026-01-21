For a variety of reasons the following system level acceptance tests cannot be performed in an automated environment so the steps are laid out below.

**HUD Control**

The following tests require installation of the app, acceptance of all permissions, connection of a bluetooth mouse/pointer device and launching of the HUD

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| scroll wakeup | 1\. Wait 10 seconds for menu bar to disappear <br>2\. Place cursor at bottom of screen <br>3\. Scroll mouse wheel | Menu bar is displayed | Pass |
| click wakeup | 1\. Wait 10 seconds for menu bar to disappear <br>2\. Left click in center of HUD | Menu bar is displayed | Pass |
| voice wakeup | 1\. Wait 10 seconds for menu bar to disappear <br>2\. Say “Glimpse”	 | Menu bar is displayed | Pass |
| scroll navigate | 1\. Ensure menu bar is awake <br>2\. Place cursor at bottom of screen <br>3\. Slowly scroll mouse wheel until selected widget returns to start | Selected widget is changed on menu bar and widget contents change accordingly in sync with mouse | Pass |
| click navigate | 1\. Ensure menu bar is awake <br>2\. Left click on a different widget in menu bar <br>3\. Repeat until all widgets have been visited | Widget contents and selected widget change according to selection | Pass |
| voice navigate | 1\. Say “Glimpse, \<widget\_name\>” <br>2\. Repeat for all widgets | Widget contents and selected widget change according to selection | Pass |
| voice invalid navigate | 1\. Say “Gimpse Android” | Verify widget does not chage | Pass |
| brightness slider control | 1\. Start Settings widget <br>2\. Slide brightness slider to max and then min | Verify phone brightness responds according to slider | Pass |
| brightness voice control | 1\. Say “Glimpse brightness 100” <br>2\. Say “Glimpse brightness 0” | Verify brightness responds accordingly | Pass |
| volume slider control | 1\. Start settings widget <br>2\. Slide volume to max <br>3\. Slide volume to min | Verify volume change after each step (do this by using chat tts or by pressing volume button on phone) | Pass |
| hud off | 1\. With the hud on, double click on center of screen <br>2\. single click once on center of screen | Verify hud menu does not display and hud is not responsive to voice commands | Pass |
| hud on | 1\. With the hud off, double click on center of screen <br>2\. single click once on center of screen | Verify hud menu displays | Pass |

**Notification**  
The following tests require installation of the app, acceptance of all permissions, connection of a bluetooth mouse/pointer device and launching of the HUD as well as a reliable way to produce notifications on the device (such as discord messages)

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| notification appears | 1\. Trigger a notification on the phone <br>2\. Wait 7 seconds  | Verify notification appears in HUD and then disappears after 7 seconds | Pass |
| notification dismiss swipe | 1\. Trigger a notification on the phone <br>2\. Swipe down on notification | Verify notification disappears after swiping | Pass |
| notification dismiss click | 1\. Trigger a notification on the phone <br>2\. Click the X on the notification | Verify notification disappears after clicking | Pass |
| notification dnd | 1\. Start settings widget <br>2\. Trigger a notification on the phone | Verify notification does not appear | Pass |

**Facial recognition**  
The following tests require installation of the app, acceptance of all permissions, connection of a bluetooth mouse/pointer device.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| add face from gallery | 1\. Navigate to face upload <br>2\. Select choose from gallery <br>3\. Select photo  | Verify photo is displayed | Pass |
| add face from photo | 1\. Navigate to face upload <br>2\. Select take picture <br>3\. Take photo | Verify photo is displayed | Pass |
| add person | 1\. Navigate to face upload <br>2\. Enter name & info and select photo <br>3\. Select submit <br>4\. Navigate to list people | Verify person exists | Pass |
| delete person | 1\. Create a person <br>2\. Navigate to list people <br>3\. Click X on person | Verify person is removed from list | Pass |
| detect person | 1\. Create a person whose face is available <br>2\. Start Facial recognition in HUD <br>3\. Point camera at person’s face <br>4\. Point camera at no faces | Verify name & info are displayed and then disappear once no longer pointed at face | Pass |
| unrecognized person | 1\. Start facial recognition in HUD <br>2\. Point camera at a person whose face is not in the database | Verify “Do not know” is displayed | Pass |

**Compass**  
The following tests require installation of the app, acceptance of all permissions, and launching of the HUD.   
*Note: Compass accuracy can be impacted by proximity to other electronic devices*

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| compass standard | 1\. Start compass widget <br>2\. Hold phone with screen perpendicular to ground <br>3\. Slowly turn in a circle | Verify compass headings are accurate, for direction of rear camera, and change as turning.  | Pass |
| Compass alternate | 1\. Start compass widget <br>2\. Hold phone with screen facing ground <br>3\. Slowly phone in a circle | Verify compass headings are accurate, for direction of right edge of phone when held in standard portrait orientation, and changes as turning.  | Pass |

**Chat**  
The following tests require installation of the app, acceptance of all permissions, connection of a bluetooth mouse/pointer device and launching of the HUD with the chat widget selected

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| chat pointer | 1\. Left click on upper part of screen <br>2\. Say “Tell me a fun fact” | Verify … displayed after click and a response other than "Sorry, please ask something else" | Pass |
| chat image pointer | 1\. Point rear camera at something <br>2\. Right click on upper part of screen | Verify displayed response describes (roughly) what the camera sees | Pass |
| chat voice | 1\. say “Gemini, tell me a fun fact” | Verify … displayed after saying Gemini Verify response other than "Sorry, please ask something else"  | Pass |
| chat image voice | 1\. say “Gemini, what do you see in this picture” | Verify … displayed after saying Gemini Verify displayed response describes (roughly) what the camera sees | Pass |
| chat scroll | 1\. say “Gemini, tell me a really long sentence” <br>2\. Scroll with pointer device on the response | Verify response scrolls | Pass |

**Navigation**

The following tests require installation of the application, acceptance of all permissions, and connection of a Bluetooth mouse/pointer device.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| valid destination | 1\. From Glimpse home screen, click **Destination Input**. <br>2\. Enter **Engineering Teaching Learning Complex** in the text box. <br>3\. Click the search result that matches the inputted text. <br>4\. Click **Confirm**. <br>5\. Launch **HUD**. <br>6\. Start **Navigation** widget. | Verify turn-by-turn direction to ETLC appears | Pass |
| invalid destination | 1\. From Glimpse home screen, click **Destination Input**. <br>2\. Enter **fakelocationfakelocation** in the text box. <br>3\. Click the Home tab from the bottom bar. <br>4\. Launch **HUD**. <br>5\. Start **Navigation** widget. | Verify Confirm button does not appear on the destination input screen after filling in the text box. <br>Verify “No destination set” appears when the navigation widget starts. | Pass |
| clear valid destination | 1\. From Glimpse home screen, click **Destination Input**. <br>2\. Enter **Engineering Teaching Learning Complex** in the text box. <br>3\. Click the search result that matches the inputted text. <br>4\. Click **Confirm**. <br>5\. Click **Destination Input.** <br>6\. Click **Clear Destination**. <br>7\. Click the Home tab from the bottom bar. <br>8\. Click **Destination Input**. | The text box on the destination entry form should be empty | Pass |

**Weather**

The following tests require installation of the application, acceptance of all permissions,  connection of a Bluetooth mouse/pointer device, and launching the HUD.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| weather | 1\. Start **Weather** widget | Verify weather shows temperature, location, and conditions. *Note: the location listed on the display may not be accurate with the actual location of the device. This is an error with OpenWeatherMap’s classification of coordinates, **NOT** an error in the coordinates of the phone.* | Pass |

**Barcode**  
The following tests require installation of the application, acceptance of all permissions, and connection of a Bluetooth mouse/pointer device, and selection of the Barcode widget via the widget menu.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| valid book barcode | 1\. Point rear camera at valid **book** barcode. | Verify the HUD correctly reads the name and author of the book. | Pass  |
| valid generic barcode | 1\. Point rear camera at valid **number** barcode. | Verify the HUD correctly shows the number associated with the barcode. | Pass |
| invalid barcode | 1\. Point rear camera at **invalid/damaged** barcode. | Verify the HUD displays no text. | Pass |
| no barcode | 1\. Place **no** barcode in front of the rear camera. | Verify the HUD displays no text. | Pass |
| consecutive scan | 1\. Point rear camera at valid **book** barcode. <br>2\. Point rear camera at valid **number** barcode. | Verify the HUD correctly reads the name and author of the book. Then verify the HUD correctly shows the number associated with the barcode when the second barcode is scanned. | Pass |
| consecutive invalid scan | 1\. Point rear camera at valid **book** barcode. <br>2\. Place **no** barcode in front of the rear camera. | Verify the HUD correctly reads the name and author of the book.  Then verify the HUD displays no text. | Pass |

**HUD Editor**  
The following tests require installation of the application, acceptance of all permissions, and connection of a Bluetooth mouse/pointer device, and selection of the HUD Editor screen via the Edit Screen.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| reorder menu | 1\. Hold right hamburger of Barcode widget under “Customize Widget Menu” <br>2\. Drag downwards to reorder the widget. <br>3\. Press “Save”. | Verify that the order of widgets matches the order of the widget menu in the preview. Then navigate to the HUD and verify that the preview matches. | Pass |
| disable widget | 1\. Uncheck the box to the left of the Barcode widget. <br>2\. Press “Save”. | Verify that the widget was removed from the widget menu in the preview. Then navigate to the HUD and verify that the preview matches. | Pass |
| assign widget single | 1.Click on the dropdown under “Widget Assignments” <br>2\. Select the “Bottom” location. <br>3\. Press Save. | Verify that the widget is displayed in the bottom location of the grid. Then navigate to the HUD and verify that the preview matches. | Pass |
| reorder multi menu | 1\. Disable the “Single Widget Mode” switch. <br>2\. Hold right hamburger of Barcode widget under “Customize Widget Menu” <br>3\. Drag downwards to reorder the widget. <br>4\. Press “Save”. | Verify that the order of widgets matches the order of the widget menu in the preview. Then navigate to the HUD and verify that the preview matches. | Pass |
| disable multi widget | 1\. Disable the “Single Widget Mode” switch. <br>2\. Uncheck the box to the left of the Barcode widget. <br>3\. Press “Save”. | Verify that the widget was removed from the widget menu in the preview. Then navigate to the HUD and verify that the preview matches. | Pass |
| assign widget multi | 1\. Disable the “Single Widget Mode” switch. <br>2\. In “Widget Assignments”, click the “Top” dropdown. <br>3\. Assign Compass to “Top”. <br>4\. Click the “Bottom” dropdown. <br>5\. Assign Weather to “Bottom”. <br>6\. Click the “MiddleLeft” dropdown. <br>7\. Assign Navigate to “MiddleLeft” 8.Press “Save”. | Verify that the assigned widgets are displayed in their respective location after navigating to the HUD. | Pass |
| reset | 1\. Scroll to the bottom of the screen, press “Reset to Defaults”. | Verify that Single Widget mode is enabled, all checkboxes are checked, and Widget Assignment is set to “Top”. <br>Verify widgets are in the following order:<br>Barcode, Chat, Compass, Face, Navigate, Weather, Settings. | Pass |

**Permissions**

Each of the following tests require **a fresh installation** of the application, prior to following the steps.

| Test Name | Steps | Acceptance Criteria | Result |
| :---- | :---- | :---- | :---- |
| deny location | 1\. Launch Glimpse. <br>2\. **Don’t allow** Glimpse access to location.<br>3\. Allow Glimpse to all other prompted permissions **while using the app**. <br>4\. Allow notification access to Glimpse <br>5\. Allow Glimpse to modify system settings. <br>6\. If the application requests location permissions again, click **Don’t allow**. | “Missing permission: Precise Location” Toast message appears  | Pass |
| deny camera | 1\. Launch Glimpse. <br>2\. **Don’t allow** Glimpse camera access. <br>3\. Allow Glimpse all other prompted permissions **while using the app**. <br>4\. Allow notification access to Glimpse. <br>5\. Allow Glimpse to modify system settings. <br>6\. If the application requests camera permissions again, click **Don’t allow**. | “Missing permissions: Camera” Toast message appears  | Pass |
| deny audio | 1\. Launch Glimpse. <br>2\. **Don’t allow** Glimpse audio access. <br>3\. Allow Glimpse all other prompted permissions **while using the app**. <br>4\. Allow notification access to Glimpse. <br>5\. Allow Glimpse to modify system settings. <br>6\. If the application requests audio permissions again, click **Don’t allow**. | “Missing permissions: Microphone” message appears | Pass |
| deny notifications | 1\. Launch Glimpse. <br>2\. Allow Glimpse all prompted permissions **while using the app**. <br>3\. Do **not** allow notification access to Glimpse. | App will not let you continue without allowing notification access | Pass |
| deny system settings | 1\. Launch Glimpse. <br>2\. Allow Glimpse all prompted permissions **while using the app**. <br>3\. Allow notification access to Glimpse. <br>4\. Do **not** allow Glimpse to modify system settings. | App will not let you continue without allowing system settings access | Pass |

