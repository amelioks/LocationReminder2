# Location Reminder

ToDo list app with location reminder which reminds the user to do something when the user reaches a certain location. The application will ask the user to create an account and login and input the ToDo at the desired location. The user login account allows the user to access previously saved todo list again.

# Project Instruction

1. Create a Login screen to ask users to login using an email address or a Google account using Firebase Console. Upon successful login, navigate the user to the Reminders screen. If there is no account, the app should navigate to a Register screen.
2. Create a Authentication screen to allow a user to register using an email address or a Google account.
3. Create a screen that displays the reminders retrieved from local storage. If there are no reminders, display a "No Data" indicator.
4. Create a screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder. Do not forget to add google map API key from Google API Console
5. Create a screen to add a reminder when a user reaches the selected location. Each reminder should include 
    a. title 
    b. description 
    c. selected location
6. Reminder data should be saved to local storage.
7. For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area. when the user clicks the geofencing notification, the user will be directed to the Reminder Description screen.
8. Provide testing for the ViewModels, Coroutines and LiveData objects.
9. Create a FakeDataSource to replace the Data Layer and test the app in isolation.
10. Use Espresso and Mockito to test each screen of the app: 
    a. Test DAO (Data Access Object) and Repository classes. 
    b. Add testing for the error messages. 
    c. Add End-To-End testing for the Fragments navigation.

# How to Test the App
1. Turn allow mock locations. Settings-> Developer options-> Allow mock locations
2. Use the app Lockito to simulate a route for testing Geofencing
3. Run the location reminder application as usual and input a reminder according to the route that was previously made in Lockito app
4. The geofence will be triggered and a notification will appear
5. Click the notification and reminder details screen will appear

# Sources
1. Log in with Firebase UI: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/08385552-25d3-44dc-b66f-ae9b193d7468/concepts/63449085-cdee-4198-8f3a-745e06115db7
2. Gmaps: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/cd348783-4ee1-4016-aeea-b4dae2b3f5c0/concepts/4dcbb390-f339-44a0-9e45-01056bcb6483
3. Foregroud and Background Location Permission: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/fcb60bb3-395d-411f-8553-d18be1305942/concepts/23708933-f4f3-4c62-81fb-28f8e9adb86f
4. Adding Geofence: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/fcb60bb3-395d-411f-8553-d18be1305942/concepts/939c4bb2-0fb8-43f6-b130-c49ac905f8f9
5. Unit Test: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/116a8d85-27ec-40c0-8661-e5d6c7464e38/concepts/cae88a7c-70d8-4fa4-8254-cda4c6558c7f
6. Android Test: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/44a2e56f-8138-4bb9-83d9-af85371de022/concepts/883ac4dc-b05e-476f-8b39-efd46b1de201
