# Sagiri User Guide

Sagiri is a desktop task manager with a chat-style interface. It helps you track todos, deadlines, and events using short text commands.

## Table of Contents

- [Quick Start](#quick-start)
- [Command Summary](#command-summary)
- [Features](#features)
	- [View all tasks: `list`](#view-all-tasks-list)
	- [Add a todo: `todo`](#add-a-todo-todo)
	- [Add a deadline: `deadline`](#add-a-deadline-deadline)
	- [Add an event: `event`](#add-an-event-event)
	- [Mark a task as done: `mark`](#mark-a-task-as-done-mark)
	- [Mark a task as not done: `unmark`](#mark-a-task-as-not-done-unmark)
	- [Delete a task: `delete`](#delete-a-task-delete)
	- [Check tasks by date: `check`](#check-tasks-by-date-check)
	- [Find tasks by keyword: `find`](#find-tasks-by-keyword-find)
	- [Show help: `help`](#show-help-help)
	- [Exit the app: `bye`](#exit-the-app-bye)
- [Data Storage](#data-storage)
- [Command Notes](#command-notes)

## Quick Start

1. Ensure you have **Java 17** installed.
2. Open the project in a terminal.
3. Run the app:

	 ```bash
	 java -jar sagiri.jar
	 ```

4. Enter commands in the input box at the bottom of the GUI.

## Command Summary

| Action | Command Format |
|---|---|
| List tasks | `list` |
| Add todo | `todo <description>` |
| Add deadline | `deadline <description> /by <dd-mm-yy>` |
| Add event | `event <description> /from <dd-mm-yy> /to <dd-mm-yy>` |
| Mark done | `mark <task number>` |
| Mark not done | `unmark <task number>` |
| Delete task | `delete <task number>` |
| Check tasks on date | `check <dd-mm-yy>` |
| Find by keyword | `find <keyword>` |
| Show help | `help` |
| Exit app | `bye` |

## Features

### View all tasks: `list`

Shows all tasks with numbering.

**Format:**
`list`

### Add a todo: `todo`

Adds a basic task without a date.

**Format:**
`todo <description>`

**Example:**
`todo read CS2103T notes`

**Expected outcome:**
Sagiri adds the task and shows the updated task count.

### Add a deadline: `deadline`

Adds a task with a single due date.

**Format:**
`deadline <description> /by <dd-mm-yy>`

**Example:**
`deadline submit iP /by 15-03-26`

**Expected outcome:**
Sagiri adds a `[D]` task and displays the due date in a readable format.

### Add an event: `event`

Adds a task with a start and end date.

**Format:**
`event <description> /from <dd-mm-yy> /to <dd-mm-yy>`

**Example:**
`event NUS Hackathon /from 20-03-26 /to 22-03-26`

**Expected outcome:**
Sagiri adds an `[E]` task and shows both start and end dates.

### Mark a task as done: `mark`

Marks a task as completed.

**Format:**
`mark <task number>`

**Example:**
`mark 2`

**Expected outcome:**
Task #2 is marked with `[X]`.

### Mark a task as not done: `unmark`

Marks a task as incomplete again.

**Format:**
`unmark <task number>`

**Example:**
`unmark 2`

**Expected outcome:**
Task #2 is marked with `[ ]`.

### Delete a task: `delete`

Removes a task permanently from the list.

**Format:**
`delete <task number>`

**Example:**
`delete 3`

**Expected outcome:**
Task #3 is removed from the list.

### Check tasks by date: `check`

Shows tasks that fall on a specific date:
- deadline tasks with matching `by` date
- event tasks with matching `from` or `to` date

**Format:**
`check <dd-mm-yy>`

**Example:**
`check 22-03-26`

**Expected outcome:**
Sagiri lists matching tasks for that date, or reports none found.

### Find tasks by keyword: `find`

Searches task descriptions using case-insensitive matching.

**Format:**
`find <keyword>`

**Example:**
`find report`

**Expected outcome:**
Sagiri lists tasks whose names contain the keyword.

### Show help: `help`

Displays the built-in command list.

**Format:**
`help`

### Exit the app: `bye`

Closes the application.

**Format:**
`bye`

## Data Storage

- Tasks are automatically saved to `data/Sagiri.dat`.
- The file is created automatically when needed.
- Existing tasks are loaded when the app starts.

## Command Notes

- Date format is strictly `dd-mm-yy` (for example, `05-09-26`).
- Commands are lowercase and must follow the shown spacing.
- Task numbers are based on the latest `list` output and start from `1`.
