
/// Represent permission of location accuracy is approved
/// 
/// - `precise`: user approve a precise permission to your application.
/// - `approximate`: user only approve a coarse accuracy permission.
/// - `denied`: user does not allow access location information.
enum LocationAccuracyPermission {
  precise, // manifest android.permission.ACCESS_FINE_LOCATION
  approximate, // manifest android.permission.ACCESS_COARSE_LOCATION
  denied
}
