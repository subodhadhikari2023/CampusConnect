# CampusConnect — User Manual

## Overview

CampusConnect is a role-based collaborative learning platform for academic institutions. There are four roles: **Admin**, **HOD** (Head of Department), **Teacher**, and **Student**. Each role has a dedicated dashboard and a set of permitted actions.

**Default credentials** (password `password` for all dummy accounts):

| Username | Role    |
|----------|---------|
| admin1   | Admin   |
| admin2   | Admin   |
| hod1     | HOD     |
| hod2     | HOD     |
| teacher1 | Teacher |
| teacher2 | Teacher |
| student1 | Student |
| student2 | Student |

---

## Admin

### Dashboard

Displays total counts of users, departments, and courses registered in the system. Cards link to the corresponding management pages.

### Manage Users

**View:** `/admin/user-management` lists all registered users with their department and role.

**Add a user:**
1. Click **Add User**.
2. Fill in username, email, password, role, and department.
3. Click **Save**. The password is stored with the `{noop}` dev prefix automatically.

**Edit a user:**
1. Click the **Edit** icon next to a user.
2. Update email, department, or role. Leave the password field blank to keep the existing password.
3. Click **Save**.

**Delete a user:**
1. Click the **Delete** icon and confirm.
2. Deletion fails with an error if the user has associated records (e.g., uploaded files).

### Manage Departments

**View:** `/admin/manage-department` lists all departments.

**Add a department:**
1. Click **Add Department** and enter a unique name.
2. Click **Save**.

**Delete a department:**
- Deletion is blocked if the department has members or courses.

### Profile

- **View:** `/admin/profile` shows the current admin's username, department, and email.
- **Edit email:** Update the email field and click **Save Changes**.
- **Change password:** Fill in **New Password** and **Confirm Password** and click **Save Changes**. Leave both blank to keep the current password. Passwords must match.

---

## HOD (Head of Department)

The HOD manages the curriculum, members, and announcements for their own department. All data is automatically scoped to the HOD's department — they cannot view or modify other departments.

### Dashboard

Displays the department name, total faculty, total students, and total courses.

### Courses

**Add a course:** `/hod/add-course` → enter a course name → Save. You are redirected to the semester setup page for the new course.

**Manage courses:** `/hod/manage-course` lists courses. Use **Edit** to rename or **Delete** to remove (deletion blocked if the course has semesters).

### Semesters

**View:** `/hod/semesters?courseId=<id>` lists semesters for a course.

**Add:** Enter a semester name in the inline form and click **Add**.

**Rename:** Click **Edit** next to a semester, change the name, and click **Save**.

**Delete:** Click **Delete** — blocked if the semester has subjects.

### Curriculum

`/hod/curriculum?courseId=<id>` shows all semesters and their subjects in one page.

**Add a subject:** Enter the subject name under the correct semester row and click **Add Subject**.

**Remove a subject:** Click the **Remove** button next to a subject.

### Manage Subjects

`/hod/manage-subject?courseId=<id>` provides a table view of all subjects grouped by semester.

**Rename a subject:** Click **Edit**, update the name, click **Save**.

**Delete a subject:** Click **Delete** — this also removes any teacher assignment for that subject.

### Assign Teachers

`/hod/assign-teachers?courseId=<id>` shows each subject with its currently assigned teacher.

**Assign:** Select a teacher from the dropdown and click **Assign**.

**Remove assignment:** Select the blank option and click **Assign**.

Only teachers in the same department appear in the dropdown.

### Department Files

`/hod/department-files` shows all files uploaded by teachers in the department, with course, semester, subject, uploader, and upload date.

### Members

`/hod/members` lists all faculty and students registered in the department.

### Announcements

**View:** `/hod/announcements` lists all department announcements.

**Post:** Fill in the title and body in the inline form and click **Post Announcement**.

**Edit:** Click **Edit** next to an announcement, update the fields, and click **Update**.

**Delete:** Click **Delete** next to an announcement.

### Profile

Same as Admin profile — update email and/or password from `/hod/profile`.

---

## Teacher

The Teacher can upload and browse learning resources scoped to the courses they are assigned to teach.

### Dashboard

`/teacher` shows quick-access cards for each resource category (PPTs, Notes, Programs, Audio Books, Reference Books, Videos). Announcements from the teacher's department appear below.

### Upload Resources

`/teacher/upload` (or the category-specific links)

1. Select **Course**, **Semester**, **Subject**, and **Category**.
   - Only courses, semesters, and subjects from the teacher's own assignments appear.
2. Click **Choose File** and select the file to upload.
3. Click **Upload**. The file is saved to the server and recorded in the database.

Attempting to upload for a subject you are not assigned to returns an error.

### Browse Resources

`/teacher/browse` lets the teacher search for files uploaded by anyone in their department.

1. Optionally filter by **Category**, **Course**, **Semester**, or **Subject**.
2. Click **Search**.
3. Download in the original format, GZIP-compressed, or ZIP-compressed using the buttons in the results table.

### Profile

`/teacher/profile` — update email and/or password. Same interaction as Admin and HOD.

---

## Student

Students can browse and download resources uploaded by teachers in their department. They cannot upload files.

### Dashboard

`/student` shows resource-category cards and department announcements. Click a card to go directly to the browse page filtered to that category.

### Browse Resources

`/student/browse`

1. The **Department** field is pre-filled and read-only (students are always scoped to their own department).
2. Optionally filter by **Category**, **Course**, **Semester**, or **Subject**.
3. Click **Search**.
4. The results table shows matching files. Click **Original**, **GZIP**, or **ZIP** to download.

### Profile

`/student/profile` — update email and/or change password. Leave the password fields blank to keep the current password. Both fields must match to change the password.

---

## Common Notes

- **Session:** You are automatically logged out after closing the browser (session-based authentication). Use the **Logout** button in the header to end your session explicitly.
- **File size:** Single-file uploads are capped at 1 GB. Reduce files before uploading if needed.
- **Supported files:** Any file type is accepted; the category (PPTs, Notes, etc.) is chosen by the uploader.
- **Password storage:** Passwords are stored with a `{noop}` prefix in development mode (plain-text). This is intentional for a demo environment and must be replaced with BCrypt in production.
