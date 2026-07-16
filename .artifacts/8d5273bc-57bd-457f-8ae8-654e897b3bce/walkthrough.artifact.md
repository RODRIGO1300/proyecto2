# Walkthrough - Black Text for Login and Register Fields

I have updated the `LoginScreen.kt` and `RegisterScreen.kt` files to ensure that all `OutlinedTextField` components use black for their text and labels.

## Changes

### [LoginScreen.kt](file:///C:/Users/omen/Desktop/Universidad/CURSOS VERANO/Proyeto2/app/src/main/java/com/example/proyeto2/ui/screens/LoginScreen.kt)
- Added `OutlinedTextFieldDefaults.colors` to all `OutlinedTextField` instances.
- Set `focusedTextColor`, `unfocusedTextColor`, `focusedLabelColor`, and `unfocusedLabelColor` to `Color.Black`.
- Set `focusedBorderColor` to `Color.Black` for better visibility.

### [RegisterScreen.kt](file:///C:/Users/omen/Desktop/Universidad/CURSOS VERANO/Proyeto2/app/src/main/java/com/example/proyeto2/ui/screens/RegisterScreen.kt)
- Applied the same color configuration to all three input fields (Username, Email, and Password).

## Verification Results

### Manual Verification
- The text and labels in the Login and Register screens are now explicitly black, making them readable on the light-colored surface.
