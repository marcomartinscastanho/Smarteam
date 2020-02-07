# Smarteam #
Android app to manage Sunday league games with your friends

## Table of Contents ##
1. [Motivation](#motivation)
2. [Objectives and Structure](#objectives-and-structure)
3. [What's left?](#whats-left)
4. [Future ideas](#future-ideas)

## Motivation ##
A while ago I used to play 5-a-side football weekly with my college friends. Every week we had the same problem: we had no established teams, so someone had to decide who faced who. No one wanted to have the task of drawing their friends into teams because in the end everyone would complain that the teams were unbalanced and the game was not even. This really bothered me since it would delay the start of the game and make for games to often be very one-sided.

In order to solve this, I came up with an idea for automating the teams drawing process and making teams even. The idea was to set a score to every player, based on their win percentage so far (I started recording the results every week), and allocating players into teams making sure the total score of both teams would be as close as possible. Also, I would publish the players ranking on our facebook group after every game, so everyone knew how good or bad they were performing.

Over time, I adjusted the score formula in order to get a more reliable estimate of a players recent form and its expected performance, which consisted in attributing points to results (ex: 3 per win, 1 for draw, 0 for absence, -2 for defeat) and making a weighted average giving a higher weight to latest results.

Eventually, our weekly games became very even and competitive, since no one wanted to see their names on the bottom half of the weekly ranking. My friends who initially joked on my method now praised it for making games more interesting.

The first version on this tool was made in Excel, in a very user-unfriendly way that only I could use. When I start playing with my IT-work colleagues, I thought about using the tool here too, but I could not show them such an ugly thing. Therefore I decided to migrate it to a more presentable platform. I had been thinking about learning Android Development for a while, and I decided to learn on the job while re-factoring the teams allocation tool.

I made a first version, a very amateur and low performing one, with data stored in text files since I didn't know Android supported SQL Lite. Later I discarded this version and started a new one from scratch, this time trying to make it cleaner and faster and more ambitious.

I really enjoy developing for Android and while I was developing this App I thought I could make this available on Play Store. Even if no one would ever used, at least I could show it on my cv. But working 8h a day as an IT developer and go home to work a few more hours on Android is exhausting and social life-consuming, so the progress of this App stalled, as you can see on the commits history.

First I had all code private since I wanted to do this all by myself, I was very protective of my baby and didn't want any external influence on this. But I realized that it's better to allow someone else to contribute to finish the App than to keep it locked in my computer and never finishing it.


So, if you're interested in contribute to finish the development of this Android App, here are some ground ideas and design I would like to keep.

## Objectives and Structure ##
As explained before, the objective of this App is to keep track of the performance of every **Player** in a group of friends you play with (here I call it a **Team**), generating balanced teams for future games based on the Score of each participating player and keeping a Ranking of the Team Players.

The **Main Menu** Activity has 4 buttons:
* New
* Load
* Delete
* Settings

**New**, **Load** and **Delete** here refers to Teams, in case you have regular games with more than a group of friends (ex: former college friends; current work colleagues). Both this 3 buttons open a dialog in which the user can set a name to a new Team or select an existing one to Load or Delete. The **Settings** button goes to a **Settings Menu**, in which the user can set different values to Wins, Draws, Defeats, etc. or restore the default ones.

After creating a New Team or Loading an existing one, a different Activity is opened. I called this the **Team Menu** Activity. This activity has 5 buttons:
* Results
* Ranking
* Lineups
* Statistics
* Edit

Let's start from the top:
The Results button opens another a **Team->Results Menu** Activity which allows to manage the games results. It has 4 buttons:
* Add, to add a new result
* View, to view all results
* Edit, to edit a result (I haven't decided yet if I will keep this)
* Delete, to delete a result (same as before)

The **Ranking** button just shows an Activity with the ranking of the Team's players, with each line of the table containing the Player's position, its name and its Score. This Activity has a Share Button on the Action Bar, allowing the user to share a snapshot of the Ranking on several platforms such as Facebook, WhatsApp, Twitter or e-mail.

The **Lineups** button is where the magic happens. It shows a dialog asking the user to select the players who will play on the next game, upon confirmation, opens a new activity showing the allocation of those players into 2 groups with average scores the closest possible. Like in the Ranking activity, this is also shareable.

The **Statistics** button opens a **Statistics Menu** Activity showing statistics that I haven't quite decided about. It will be something like "Player with most games", "Player with most win", "Player with most defeats in a row", etc. Also shareable.

The **Edit** button opens an **Edit Menu** Activity that allows the user to manage the Team. It has 4 buttons:
* Rename Team
* Add Player
* Rename Player
* Delete Player

All are self-explanatory


## What's left? ##
* The basic functions are mostly working properly, except for some likely not yet identified bugs;
* As mentioned, the Statistics Menu is not done yet, not even started. I have done however a set of methods with queries that may be useful.
* I thought about replacing the structure of the Results Menu with a Card List, with each game result (the date, result and players involved) presented in a card, and the options to Edit or Delete a result being prompted upon tapping on the corresponding card. The Add button would be a floating round button. I actually begin implementing this idea, but it is only half done and not working, so I haven't commited it yet.

I have a few more ideas for this, which are listed below, but right know I just want to finish a first fully working version of this App.


## Future ideas ##
### Smarteam Social ###
#### New name proposals ####
- **GameSquad**
- ...
#### Requirements and Walkthrough ####
- Registration
  - Phone number validated registration (similar to WhatsApp) - under consideration, for cheating prevention
    - Prevent users from having multiple accounts and create fake Matches to impact their Scores
    - Prevent users from deleting their accounts and starting fresh with the default Score, erasing their history of Match Results
  - Users must register - they become Players
  - Upon registered, Players must choose a unique @handle
- Player
  - Players can edit their personal info
    - Name
    - Age
    - Picture
    - Location
    - Unique @handle
  - All Players have a global Score
    - Every Player starts with a default Score
    - The Score is determined by accummulation of points
    - Players win or lose points after each Match played
    - Points added or deducted from the Player's Score after each Match depend on the result the Player obtained in that Match (win/draw/defeat)
    - Points added or deducted from the Player's Score after each Match depend on the difference between the Overall Scores of the Squads playing that Match (TODO: to be reviewed... if Squads are balanced, this doesn't make much sense...)
      - e.g. winning against a weaker Squad awards fewer points than winning against a stronger Squad
      - e.g. losing against a weaker Squad deducts more points than losing against a stronger Squad
  - The Unique @handle is public
  - The Score is public
  - Number of Matches played is public
  - Visibility of all other Player information can be configured by the Player
  - Players can set configurations such as Block Invites from Teams, change visibility of Personal Info, etc.
  - Players can be members of multiple Teams (a maximum number is under consideration)
- Teams
  - All Players can create Teams
  - The creator of a Team is the default Admin
  - The creator of a Team must define a name for the Team
  - The creator of a Team can define additional Team information such as Picture, Description, Location, etc.
  - Admins can invite Players (by their @handle) to the Team
  - Admins can elevate other Players in the Team to Admin
  - Admins can remove Players from the Team
  - Matches
    - Admins can book Matches
    - Matches must have a Date and Time (set in the future), set upon creation of the Match
      - validation of multiple matches at similar datetimes under consideration, to avoid cheating
    - Matches can have a Location, set at any time
    - Admins can edit the Match info (Date, Time and Location) for Matches "not yet played"
    - Admins can call Team Players to Matches "not yet played"
      - Called Players receive notifications of invite to Matches
      - Called Players are automatically added to the list of called players to the Match
      - No presence confirmation status for Team Players, means Players in the Team don't need to confirm they'll be present
    - Admins can un-call Players from Matches "not yet played"
    - Players can un-call themselves from Matches "not yet played"
    - Guest Players
      - Guest Players are registered Players that are not part of the Team
      - Admins can invite Guest Players for individual Matches, using their unique @handle
      - Guest Players are notified of invitations to Matches
      - Guest Players can accept or reject invitations to Matches
      - Guest Players are added to the Match list of called Players only if and when they accept the invitation
      - Guest Players don't become part of the Team
      - Guest Players are not displayed in the Team Ranking
      - Guest Players Statistics are not accounted in the Team Statistics
    - Ghost Players - name under consideration
      - Ghost Players are unregistered players
      - Admins can add Ghost Players to Matches "not yet played"
      - Ghost Payers must have a name
      - Ghost Players have a default Score equal to the average Score of the Players called for the Match
      - There should be a limit to the number of Ghost Players allowed per Match (under consideration)
      - Ghost Players exist only in the scope of the single Match they were called to
      - They are not usable in other Matches
    - All Players in the Team can consult a Match
      - Match screen shows Date, Time, Location (if any) and list of called Players (if any)
      - Match screen shows the Squads, if published
      - Match screen shows the Result, if published
    - Players can Share (social media) the info of a Match "not yet played", if Share is enabled
    - Admins can publish the Match Squads at any time
      - Called Players are split in 2 Squads
      - Each Squad has an Overall Score, equal to the sum of the Scores of all Players in the Squad
      - The Overall Scores of the 2 Squads must be balanced (i.e. the difference between them must be the minimum possible)
      - An algorithm splits the Players into Squads, respecting the balance between Squads Overall Scores
      - If the number of called Players is even, the Squads must have the same number of Players
      - If the number of called Players is odd, one Squad has 1 more Player than the other
      - In any case, Overall Scores of the Squads must be balanced
      - Squads show the list of Players in it, and their individual Score
      - Squads show the Overal Score of the Squad
      - When published, Squads become are visible for everyone
      - Squads stop being visible if the list of called Players changes
      - Players can Share (social media) the Squads, if they are visible and Share is enabled
    - Admins can manually modify the Squads
      - Admin can swap a number of Players in Squad A for the same number of Players in Squad B
      - Can be done at any time, if the Squads are visible and the Match is "not yet played"
    - Admins can Cancel Matches "not yet played"
      - When cancelled, a Match is deleted
    - Admins can set the Result of Matches "not yet played"
      - Result can be:
        - Squad A wins
        - Draw
        - Squad B wins
      - Upon setting the Result, a Match becomes "played"
      - This action only becomes available x minutes (1h?) after the specified Match DateTime - avoid cheating
      - The Match Result becomes visible
      - The added or deducted points for each Squad becomes visible
      - When a Match becomes "played", all of its content can no longer be edited
      - It can however be consulted
      - Playes can Share (social media) the Results of Matches "played", if Share is enabled
  - History of Matches
    - All Players can consult the History of Matches at any time
    - A maximum of number of Matches that can be consulted is under consideration
  - Ranking
    - A Ranking of all Players in the Team, ordered by their score
    - Players can consult the Ranking at any time
    - The Ranking is updated everytime a Player score changes
    - Playes can Share (social media) the Ranking, if Share is enabled
  - Statistics
    - All Players can consult Statistics
    - Statisctics only consider Matches played inside the Team
    - Statistics may include
      - highest # of games played
      - highest # of wins
      - highest # of draws
      - highest # of losses
      - lowest of each ?
      - highest winning %
      - highest unbeaten %
      - longest winning streak
      - longest unbeaten streak
      - longest ongoing winning streak
      - longest ongoing unbeaten streak
    - Playes can Share (social media) the Statistics, if Share is enabled
  - Configurations
    - Admins can set the shareable status of:
      - Matches "not yet played"
      - Match Squads
      - Match Results
      - Team Ranking
      - Team Statistics
- Global features
  - Global Ranking, updated periodically, can be consulted at any time
  - National Ranking, updated periodically, can be consulted at any time
  - Periodic automatic Leaderboard publications on Social Media
- Languages
  - Default: English
  - Other languages should be available:
    - Portuguese
    - Spanish
    - French
    
- Additional Features under consideration
  - Team chat (WhatsApp-like)
  - Invite Registered Guests (Players not in the Team) for Matches - strongly under consideration

#### Competitors Analysis ####
- **SportEasy**
  - https://www.sporteasy.net/
  - Registerd users: ~1M (according to website)
  - Android downloads: +100k
  - iOS downloads: ?
  - Origin: France
  - Features
    - Multi-language (8 languages)
    - Multi-sports
    - Creation of Team website
    - Scheduling Team events
    - Team comminucation via e-mail, notifications and in-app comments
    - Track Players availability
    - Manually set Lineups (Squads splitting)
    - Statistics analysis
    - Manual player rating
  
- **Chega+**
  - https://chegamaisapp.com/
  - Registered users: ?
  - Android downloads: +100k
  - iOS downloads: ?
  - Origin: Brazil
  - Features
    - 1 language (Portuguese only)
    - Football only
    - Presence list
    - Field cost splitting
    - Tracking of Goal scorers
    - Notifications
    - Manual rating
    - Squad split, randomly or by Rating
  
- **Footinho**
  - https://footinho.com/
  - Registered users: ~30k (according to website)
  - Android downloads: +5k
  - iOS downloads: ?
  - Origin: France
  - Features
    - 2 languages (French and English)
    - Football only
    - Presence management
    - Man of the Match voting after each Match
    - Player invites via SMS, Notification, email, etc
    - Open Matches, publicly available

#### Added value of GameSquad ####
- Simplicity of inputs
  - Only input after each Match is the Match Result (win/draw/loss)
- Automatic Scores
  - App updates Players' Scores automatically after each Match played
- Balanced Squads feature
  - No more time spent on deciding the Squads before each Match
  - Play more challenging Matches between balanced Squads
- **Gamification**
  - Improves user engagement by defining game-design elements such as:
    - Global user score
    - Global and intra-Team rankings
    - Intra-Team Statistics
  - Social Networks integration
    - Ability to share App content on Social Networks make GameSquad improve app visibility and user competition
    - Periodic automatic publication of Global Leaderboards on Social Media
- Simplicity of use
  - Only core feature
  - Features are easy to use
  - Small ammount of inputs needed from each user
