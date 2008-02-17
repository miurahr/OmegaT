
!cd ..\..
!system "mkdir dist2"

; The name of the installer
Name "OmegaT"

; The file to write
OutFile dist2\OmegaT-1.8-install.exe

; The default installation directory
InstallDir $PROGRAMFILES\OmegaT

;--------------------------------
XPStyle on

LoadLanguageFile "${NSISDIR}\Contrib\Language Files\English.nlf"
LoadLanguageFile "${NSISDIR}\Contrib\Language Files\Belarusian.nlf"
LoadLanguageFile "${NSISDIR}\Contrib\Language Files\French.nlf"
LoadLanguageFile "${NSISDIR}\Contrib\Language Files\German.nlf"

;--------------------------------
; Pages

Page directory
Page components
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

Section "un.Uninstaller Section"
    
    ; Remove the installation directory

    RMDir /r $INSTDIR

    ; Remove the shortcuts
    
    Delete "$SMPROGRAMS\OmegaT\OmegaT.lnk"
    
    RMDir /r $SMPROGRAMS\OmegaT
  
    Delete "$QUICKLAUNCH\OmegaT.lnk"
  
    Delete "$DESKTOP\OmegaT.lnk"
    
    ; Remove the registry entries for the Add/remove programs dialogue
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName"
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString"
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon"
  
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion"
    
SectionEnd

;--------------------------------

Section "Desktop icon"

    SetOutPath $INSTDIR

    CreateShortCut "$DESKTOP\OmegaT.lnk" "$INSTDIR\OmegaT.exe"

SectionEnd

;--------------------------------

Section "Start menu folder"

    CreateDirectory $SMPROGRAMS\OmegaT
  
    SetOutPath $INSTDIR

    CreateShortCut "$SMPROGRAMS\OmegaT\OmegaT.lnk" "$INSTDIR\OmegaT.exe"

SectionEnd

;--------------------------------

Section "Quicklaunch icon"

    SetOutPath $INSTDIR

    CreateShortCut "$QUICKLAUNCH\OmegaT.lnk" "$INSTDIR\OmegaT.exe"

SectionEnd

;--------------------------------

; The stuff to install
Section "!OmegaT program"
  SectionIn RO

  ; -------------------- main jar --------------------

  SetOutPath $INSTDIR
  
  File /r dist\*.*

  File release\win32-specific\OmegaT.exe
  
  ; --------------------- make uninstaller ---------------------
  
  WriteUninstaller $INSTDIR\uninstall.exe
  
  ; --------------------------- docs -----------------------------
  
;  CreateDirectory $INSTDIR\doc\

;  SetOutPath $INSTDIR\doc\

;  File doc\html-local\*.*
  
  ; ---------------- add/remove programs entry ------------------
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName" "OmegaT 1.8"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString" "$INSTDIR\uninstall.exe"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon" "$INSTDIR\OmegaT.exe"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion" "1.8"
  
SectionEnd ; end the section
