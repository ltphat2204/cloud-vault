# UC-14: Search Files/Folders

**ID:** UC-14  
**Name:** Search Files/Folders  
**Actors:** Authenticated User  
**Preconditions:** User is logged in.  
**Postconditions:** User sees items matching the search query that they have access to.  

**Main Flow:**
1. User enters search text in the search bar.
2. User optionally selects a project/folder scope.
3. System queries the database for items where name contains the query and user has access.
4. System returns a list of matching items with their full paths.
5. Frontend displays search results.

**Exceptions:** 401, 500.
