## Changelogs

Whenever a pull request is created, we will check for the required resources. One of these is the changelog, which is a brief description of the changes made and is displayed in the Minecraft chat.

Here's an example of what to expect:

```changediff
: Below is a list of different prefixes. Any item prefixed with a colon (:) represents a note. These notes typically refer to updates related to game versions, plugin changes, or other information not included in the current list of changes found in this changelog.

# Groups
: Any elements that come after a group will be grouped. This will be used for changes to specific areas, such as player attributes or modifications to items.

# Additions
: Added elements refer to any newly introduced features, items, or blocks in the release. Here's an example of an addition: 
+ Adds comments

# Deletions
: Removed elements refer to anything that has been deleted, including locations, attributes, or other features. Here's an example of a deletion: 
- Removes config options

# Changes
: Changes to elements are typically caused by alterations to their names, file locations, or attributes. Here's an example of a change: 
~ Changes attributes on Player

# Fixes
: Fixes to elements refer to any changes made to resolve a bug or issue that has been identified. Here's an example of a fix: 
> Fixes crash issues with invalid packets

# Metadata
: You may not often need to make metadata changes, but when you do, it usually follows the format `<Key>: <Value>`. Here's a couple examples of metadata: 

$ Title: Example Release
$ Released-On: 1771639911
$ Authored-By: 12f3aa8e-26f8-4960-9298-f588337370d1 (Leialoha)
$ Co-Authored-By: 8667ba71-b85a-4004-af54-457a9734eed7 (Steve)
```

### Variables

You can use variables like `__PR__` or `__DATE__` to include your pull request number (#1) or the date when a pull request was created. Here's an example of what to expect:

```diff
- This is my pull request: __PR__
+ This is my pull request: (#2)
```