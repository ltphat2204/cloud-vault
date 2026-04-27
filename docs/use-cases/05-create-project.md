# UC-05: Create Project

**ID:** UC-05  
**Name:** Create Project  
**Actors:** Authenticated User  
**Preconditions:** User is logged in.  
**Postconditions:** A new project is created and associated with the user as owner.  

**Main Flow:**
1. User clicks "New Project" button.
2. User enters project name and optional description.
3. System validates project name (e.g., non-empty, length limits).
4. System creates a `Project` record in the database.
5. System initializes a root folder for the project.
6. System returns the new project metadata.

**Alternative Flows:**
- **3a. Invalid name:** Show validation error.

**Exceptions:** 401 (Unauthorized), 400 (Bad Request), 500.
