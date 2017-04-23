# dotfilebackup

Simple java application that copies dotfiles from various locations to target location.
The source file locations and the target file locations are stored in a settings file in the user home directory. 

Sample content of settings file:
>>_vimrc
C:\Users\olaha\_vimrc
E:\GitHub\mydotfiles\_vimrc
<<End _vimrc
>>Start .gitconfig
C:\Users\olaha\.gitconfig
E:\GitHub\mydotfiles\.gitconfig
<<End .gitconfig
>>Start powershell
C:\Users\olaha\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1
E:\GitHub\mydotfiles\Microsoft.PowerShell_profile.ps1
<<End powershell 
