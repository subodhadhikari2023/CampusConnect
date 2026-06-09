Re-read the current state of the codebase and update ARCHITECTURE.md to reflect it accurately.

Steps:
1. Read all Java source files under src/main/java/ to get the current class structure, methods, and relationships.
2. Read sql-scripts/databaseScript.sql for the current schema.
3. Read pom.xml for the project coordinates and dependencies.
4. Read docker-compose.yml for the deployment topology.
5. Update ARCHITECTURE.md in-place, preserving the four existing sections:
   - **System Architecture** (flowchart TD Mermaid diagram + layer responsibilities table)
   - **ER Diagram** (erDiagram Mermaid diagram + key design notes)
   - **Class / Component Diagram** (classDiagram Mermaid diagram)
   - **Route Map** (table of all controller endpoints)
6. Ensure all Mermaid diagrams are syntactically valid and will render on GitHub.
7. Ensure the layer responsibilities table package names match the actual src/main/java/com/bitsunisage/campusconnect/ directory structure.
8. Ensure the route map matches all @GetMapping and @PostMapping annotations in the controllers.
