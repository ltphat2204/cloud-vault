# UC-20: Preview File

**ID:** UC-20  
**Name:** Preview File  
**Actors:** Authenticated User (with VIEW permission)  
**Preconditions:** File is of a supported type (Image, PDF, Text, Video).  
**Postconditions:** User views the file content without downloading.  

**Main Flow:**
1. User clicks on a file in the list.
2. System identifies the file type.
3. System generates a temporary signed URL for the object storage.
4. Frontend renders the preview (using `<img>`, `<video>`, or an iframe/PDF worker).

**Exceptions:** 401, 403, 404, 500.
