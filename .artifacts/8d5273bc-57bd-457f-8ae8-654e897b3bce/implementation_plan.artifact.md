# Implementation Plan - Change Login and Register Text Color to Black

The goal is to ensure that all text input fields in the Login and Registration screens have black text and labels, making them clearly visible against the white-ish background.

## Proposed Changes

### UI Screens

#### [MODIFY] [LoginScreen.kt](file:///C:/Users/omen/Desktop/Universidad/CURSOS VERANO/Proyeto2/app/src/main/java/com/example/proyeto2/ui/screens/LoginScreen.kt)
- Update `OutlinedTextField` components to use `OutlinedTextFieldDefaults.colors` with `Color.Black` for text and labels.

#### [MODIFY] [RegisterScreen.kt](file:///C:/Users/omen/Desktop/Universidad/CURSOS VERANO/Proyeto2/app/src/main/java/com/example/proyeto2/ui/screens/RegisterScreen.kt)
- Update `OutlinedTextField` components to use `OutlinedTextFieldDefaults.colors` with `Color.Black` for text and labels.

## Verification Plan

### Automated Tests
- I will run a build to ensure no syntax errors were introduced.

### Manual Verification
- The user can verify that the text in the input fields is now black on both screens.
