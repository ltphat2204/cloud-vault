# UC-07: List Projects

**ID:** UC-07  
**Name:** List Projects  
**Actors:** Authenticated User  
**Preconditions:** User is logged in.  
**Postconditions:** User sees a list of projects they own or have access to.  

**Main Flow:**
1. User navigates to the dashboard/projects page.
2. System queries the database for projects where user is owner or has a share.
3. System returns a list of project metadata (name, owner, created date, etc.).
4. Frontend displays projects in a grid or list view.

**Alternative Flows:** none.

**Exceptions:** 401, 500.
