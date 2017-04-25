# dotfilebackup

Simple java application that copies dotfiles (or any kind of files) from source location(s) to target location(s).

- The source file locations and the target file locations are stored in a settings file dotfilebackupSettings.txt.
- The settings file is located in the user home directory. 
- Only updated source files are copied.
- A new line is inserted at the beginning of each target file with: `Last updated: <current date>`

The settings file stores the source-target path the following way:

```
Opening line - can be anything
Source path
Target path
Closing line - can be anything
```

Example:
```
>>Opening line. Can be anything.
C:\Users\olaha\_vimrc
E:\GitHub\mydotfiles\_vimrc
<<Closing line. Can be anything.
>>Opening line. Can be anything.
C:\Users\olaha\.gitconfig
E:\GitHub\mydotfiles\.gitconfig
<<Closing line. Can be anything.
>>Opening line. Can be anything.
C:\Users\olaha\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1
E:\GitHub\mydotfiles\Microsoft.PowerShell_profile.ps1
<<Closing line. Can be anything.
```
