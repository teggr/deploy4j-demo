# GitHub Copilot Instructions

## General Usage
- Use Copilot to assist with code completion, suggestions, and documentation.
- Follow project-specific conventions and best practices.
- Avoid generating code that violates copyright or licensing restrictions.
- Do not generate harmful, hateful, or inappropriate content.

## Project-Specific Guidelines
- This project uses Java (Spring Boot) and Docker.
- When editing files, use concise comments to indicate unchanged code (e.g., `// ...existing code...`).
- For Docker-related tasks, prefer using the provided Dockerfile over Spring Boot OCI images.
- Maven is used for builds; use plugins for Docker integration if needed (e.g., `docker-maven-plugin`).
- When generating terminal commands, ensure compatibility with Windows `cmd.exe`.

## Testing and Validation
- After making code changes, validate with compile and lint checks.
- Run existing tests to ensure robustness and catch edge cases.
- Fix any errors related to your changes before considering the task complete.

## Tool Usage
- Use Copilot tools for file edits, terminal commands, and error checking.
- Do not show code changes directly; use the appropriate tool to apply edits.
- Group changes by file and provide a short explanation for each edit.

## Communication
- Keep responses concise and impersonal.
- Do not repeat information unnecessarily.
- Only terminate a task when all requirements are fully met and validated.

## Security
- Do not expose sensitive information, such as credentials or private keys, in generated code or documentation.

---
This file provides instructions for using GitHub Copilot in this workspace. Adjust guidelines as needed for future changes.
