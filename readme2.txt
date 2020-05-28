****PROJECT GROUP NUMBER****
g2J

****TITLE****
ProJet!

****DESCRIPTION****
ProJet! aims to help people/groups organize their works easily such as projects, meetings or even surprise birthday parties! 
The app can be used by anyone, there is no solid limitation to the scale of it.
The app is planned to be on the Android platform only.

****CURRENT STATUS****
Complete!

****WHAT WORKS?****
Currently, every feature that we have declared on the project presentation has been implemented and works,
so there are no further tasks remains to be done (except for video conference feature! We did not implement it.)

****GROUP MEMBERS and CONTRIBUTIONS****

***Ömer Burak Yıldıran***
-Firebase design (Database internal structures) and implementation (as can be seen at: https://imgur.com/tgOjovV)
-UI planning for each page
-Design and implementation of following:
  *AddMemberActivity
  *AddTaskActivity
  *CreateProjectActivity
  *EditProjetActivity
  *SettingsActivity
  *ProfilePageActivity (with Berkay)
  *ProjectPageActivity (with Berkay)
  *ProjetMainPageActivity (with Berkay)
  
***Celal Berke Can***
-Firebase Login/Register/Auth implementation
-OneSignal Notification System (with Berkay)
-Design and implementation of following:
  *RegisterActivity
  *LoginActivity
  *ForgetPasswordActivity
  *NotificationsActivity(with Berkay)
  *MyNotificationClass
  *NotificationAdapter(with Berkay)
  *NotificationsReceiver (with Berkay)
  
***Yüksel Berkay Erdem***
-Firebase Real-time Data Update Implementation (with RecyclerView)
-OneSignal Notification System (with Berke)
-Design and implementation of following:
  -Nearly all of the projet.classes package
  (Member, Message, NotificationPage, ProgressBarDay, ProgressBarTask, ProJet, Task) classes
  -Nearly all of the projet.adapters package
  (CompletedTasks, Member, Message, MyTasks, Notification, ProgressBars, Projet, Task) adapters
  -Interactions of classes and the adapters with the activities(pages)
  *CompletedTasksActivity
  *MembersPageactivity
  *MyTasksActivity
  *NotificationsActivity(with Berke)
  *ProfilePageActivity(with Burak)
  *ProjetMainPageActivity(with Burak)
  *ProjectPageActivity(with Burak)
  *FragmentCurrentTasks
  *Interface: GetInformations
  
***Deniz Berkant Demirörs***
-Pop-Up dialog design & implementation (discarded later)
-Help page design & implementation
-First version of the settings page
-Some of the layout files (.xml files)

***Amir Aliyev***
-Member did not contribute-

****Software & Libraries Used****

**Softwares**
-Android Studio IDE 3.6
-GitHub Desktop 2.5.0

**Digital Platforms & Libraries**
-Google Firebase, libraries and plugins used:
     'com.google.android.material:material:1.1.0'
     'com.google.firebase:firebase-database:19.3.0'
     'com.google.firebase:firebase-auth:19.3.1'
     'com.firebase:firebase-client-android:2.3.1'
     'com.google.firebase:firebase-analytics:17.4.1'
     'com.google.firebase:firebase-firestore:21.4.3'
     'com.google.firebase:firebase-messaging:20.1.7'
     'com.firebaseui:firebase-ui-firestore:6.2.1'

-OneSignal, libraries and plugins used:
      'com.onesignal:OneSignal:3.13.2'
      plugin: 'com.onesignal.androidsdk.onesignal-gradle-plugin'
      
-Android studio, libraries used:
     'androidx.appcompat:appcompat:1.1.0'
     'androidx.constraintlayout:constraintlayout:1.1.3'
     'com.android.support:multidex:1.0.3'

****INSTRUCTIONS FOR SETUP****
1. On Android Studio
  1.1. Install Android Studio IDE (preferably version 3.6)
  1.2. Lauch Android Studio IDE and select "Import project"
  1.3. Select the cs102project\projet file on your local device and hit OK.
  1.4. Wait for gradle build to finish (the progress can be seen at the bar at the bottom of the screen).
  1.5. Create a Android Virtual Device(AVD) (for this process, follow this link: https://developer.android.com/studio/run/managing-avds)
  1.6. After the installation of the AVD, hit the "Run" button at the middle-top of the IDE to start using ProJet!
  
2. On a mobile device
  2.1. Follow the steps from 1.1 to 1.4 in the same order.
  2.2. Connect your Android device to the PC
  2.3. Search for how to enable USB debugging for your device online, and enable it.
  2.4. After enabling USB debugging, select your device's name from the bar right next to run button.
  2.5. Hit run, and click "Allow" from your mobile device to complete the installation process.
  2.6. You can start using ProJet! now, without needing the PC from your Android device.

  
