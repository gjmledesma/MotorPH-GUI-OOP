# Object-oriented Programming - MotorPH GUI Payroll System
 
| Subject | Section | Group # |
| :-----: | :-----: | :---: |
| Object-oriented Programming | A2101 | Group 5 |  

| Active Members: |
| :----: |
| Gabriel Josemaria Ledesma |
| Martin Lanze Catolos |
| Iannah Estrada |
| Nicolo Andrew Sta. Ana |

<br>

![Landing page of program](screenshots/program-landing-page.png)

<h2>Running the Application</h2>

<h3>Step 1</h3>

This program uses <b>JavaFX 23.0.1</b> components to function. To download the required components, do the following:

1. Visit the official website at [GluonHQ](https://gluonhq.com/products/javafx/), scroll down to the Downloads section, check <i>```Include archived versions```</i>. 
2. From the JavaFX version dropdown menu, select ```23.0.1```. After that, you can then pick and download the appropriate JavaFX 23.0.1 SDK components for your computer's operating system. 

![GluonHQ JavaFX downloads section](screenshots/gluonhq-javafx-downloads.png)

3. Extract the JavaFX zip file in your Downloads folder. In the extracted folder, make sure to also extract ```src.zip```. 

![Extract src.zip](screenshots/javafx-folder-inside.png)

<h3>Step 2.1</h3>

You may now download the MotorPH payroll system application by downloading the repo as ZIP.

In Netbeans, first open the main folder of the program. Then, select the Project (```MotorPHSystemOOPF```), right-click it project to go to Properties. Here, select "Run" found in the Categories list, then paste the following inside VM Options: 

    For Windows:
    --module-path "C:\Users\<NAME>\Downloads\javafx-sdk-23.0.1\lib" --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics

    For macOS:
    --module-path "/Users/<NAME>/Downloads/javafx-sdk-23.0.1/lib" --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics

    Note: Change <NAME> to your computer system's username.

| OS | Adding Parameters in VM Options  |
| :-----: | :---: |
| In Windows | ![VM Options for Windows](screenshots/vm-options-project-properties-windows-new.png) |
| In macOS | ![VM Options for macOS](screenshots/vm-options-project-properties-macos.png) |


<h3>Step 2.2</h3>

Additionally, paste just the file location path in the program's pom.xml found in the <i>Project Files</i> folder. More specifically, paste it in line 92 of the xml file where modulePath is located.

| OS | modulePath change in pom.xml  |
| :---: | :---: | 
| In Windows | ![Module Path change in pom.xml](screenshots/pom-xml-modulepath-for-windows-new.png) |
| In macOS | ![Module Path change in pom.xml](screenshots/pom-xml-modulepath-for-macos-new.png) |

<h3>Step 3</h3>

After you're done following the instructions above, make sure to select <b><i>Build Project</i></b> first, then you can select <b><i>Run Project</i></b> in Netbeans to run the program.

<br>

Note: Do NOT try to run the program by right-clicking ```MainApplication.java``` and selecting <i>Run File</i> as the program may fail to start.