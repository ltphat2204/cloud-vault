# UC-22: Pagination & Sorting

**ID:** UC-22  
**Name:** Pagination & Sorting  
**Actors:** Authenticated User  
**Preconditions:** User is viewing a list with many items.  
**Postconditions:** User can navigate and organize the view efficiently.  

**Main Flow (Sorting):**
1. User clicks on a column header (Name, Size, Modified).
2. Frontend sends request with `sort` parameter (e.g., `?sort=name,asc`).
3. System applies sorting in the database query.
4. System returns sorted items.

**Main Flow (Pagination):**
1. User scrolls to the bottom or clicks "Next Page".
2. Frontend sends request with `page` and `size` parameters.
3. System returns a slice of the total items.

**Exceptions:** 401, 500.
