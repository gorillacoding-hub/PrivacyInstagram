# Removed components and limitations

## Removed / disabled

1. Automatic GitHub update check on launch  
2. APK download to external app dir  
3. PackageInstaller session for sideload updates  
4. `REQUEST_INSTALL_PACKAGES` manifest injection  
5. Debug bridge BroadcastReceiver registration  
6. Network path tracing toggle  
7. Pretend-old-version update testing hook  
8. “Follow @feurstagram_off” post-install card  

## Not present in upstream (nothing to remove)

- Contact address-book upload feature  
- Fork-level IMEI/SIM collectors  
- Custom calling stack beyond Instagram’s own  

## Cannot claim without verified hooks

- Automatic 24h story archival from Instagram story viewer  
- Automatic 7-day retention of unsent/deleted DMs  
- In-UI “download original” media button  

Local storage APIs exist under `com.feurstagram.extension.archive` for future verified hooks.

## Permissions

Final APK permissions ≈ Instagram’s + whatever resource patches add.  
GorillaInsta **does not** add install-packages. Review the merged manifest after your local build with:

```sh
aapt dump permissions your-build.apk
# or Android Studio APK Analyzer / JADX
```

