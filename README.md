# ChatFilter
A Minecraft Paper-mc Plugin designed to filter chat messages, removing any flagged messages containing blacklisted words. Words can be added or removed dynamically through a user-friendly GUI menu.

## Features
### 🤬 Flagging Messages
Messages containing blacklisted words are automatically flagged and removed. This includes direct matches and variations with special characters.

**Example:** `banana`
- Direct match: `"banana"`
- Partial match: `"hello banana"`
- Disguised format: `"ba$n/a.n-a"`

![ApplicationFrameHost_fkFIdCEowT](https://github.com/user-attachments/assets/fc42b6a1-5cdd-4093-8717-a0289c6b1aaa)

### ➕ Adding Words
- **By Command:** `/chatfilter add [words]`
- **By GUI:**
                                                           
![TK3ugCTBXm (2)](https://github.com/user-attachments/assets/0acb48ee-fbf3-4b9c-9dc0-0053aabb5d9e)    

### ➖ Removing Words
- **By Command:** `/chatfilter remove [words]`
- **By GUI:**
                                                           
![TK3ugCTBXm (1)](https://github.com/user-attachments/assets/6ac6a686-5cd2-4196-a5b7-2afd188566bd) 

### 🔍 Searching for Words
![TK3ugCTBXm (4)](https://github.com/user-attachments/assets/936dff81-021b-42fc-b1dd-e06ee9ffd2f2)
![TK3ugCTBXm (3)](https://github.com/user-attachments/assets/3808cc8e-ae2f-47ec-af61-45f7d485a63a)

## Installation
[Modrinth](https://modrinth.com/plugin/chatfilter)

## Coming Features
- Velocity version
- Search feature through commands
- Collections
- regex support
- `*word`, `word*`, `*word*`
