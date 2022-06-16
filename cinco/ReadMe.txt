# Assignment2-Cinco

Cinco Java program for SEPM group 5

## Requirements

1. Java Development Kit (JDK) **8 or higher**.

## Important Note

Important note: myDesktop may map your Desktop to a network drive, meaning the `cinco` folder will not be found in the typical location (e.g. `C:\Users\Fred\Desktop`). You will need to identify the correct canonical path to the Desktop on your particular configuration (e.g. `\\rmit.internal\USRHome\sh3\…` etc.).

## Building

This application assumes it will be compiled on RMIT's myDesktop environment with the above requirements. It *may* run in other environments but is not supported.

1. Download and open the file `cinco.zip` in File Explorer.

2. Copy the enclosed folder `cinco` to your Desktop.

3. Open `cmd.exe`, navigate to your Desktop, and follow these steps:
    ```
    cd .\cinco\src
    javac cinco.java
    ```

## Running

1. Open `cmd.exe`, navigate to your Desktop (see note under "Building" about possible Desktop location), and follow these steps:
    ```
    cd .\cinco\src
    java cinco
    ```

2. You can use the following test users, or create your own:

    | Username       | Password | Role  | Level |
    |----------------|----------|:-----:|:-----:|
    | harrystyles    | test     | Tech  |   1   |
    | niallhoran     | horan1   | Tech  |   1   |
    | liampayne      | liam123  | Tech  |   1   |
    | louistomlinson | tommy    | Tech  |   2   |
    | zaynmalik      | zm321    | Tech  |   2   |
    | user@cinco.com | Test123  | Staff |   -   |

## Testing Options

1. Option #5 on the main menu:
	Please be aware for testing purposes, there is a hidden option on the main menu.
	if you type "5" and enter a list of pre generated tickets will load onto the
	program. This "feature" is only for testing purposes and will not be included in
	the final version.

2. Option #6 on the main menu:
    This will auto-archive all closed tickets by shifting the completion and creation dates to before the 24 hour threshold allowing you to test the archive feature without adjusting your system's clock.