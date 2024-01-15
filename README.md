# WallpaperAI

<div align="center">
  <image src="./app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp" width="144" /><br/>
  <p><b>WallpaperAI</b><br>Create your personal Wallpapers & Widgets!</p>
</div>

## Planning

### Class Diagram

![ClassDiagram](./ClassDiagram.png)

This was the class diagram in the planning phase. I have noticed that there are a few things that I've missed and that should be adjusted.

- **The `LoginActivity` is missing**  
  The login activity should be connected with the main activity. The activity should be startable in the create fragment or the settings fragment. Depends from where the user is coming.
- **The `GenerateImageActivity` should be `GenerateActivity`**  
  I renamed it, because I think it's easier.
- **The `CreatePageFragment` also uses `DatabaseService`**  
  When you go to the Create page, it has to detect if the user entered an api key already. For this the database service has to be called to check this. If the user hasn't entered a key, it starts the LoginActivity.
- **`hasApiKey()` method on the `DatabaseService`**  
  I realized that it would be easier, if there was another method to check if the api key was set. Then I don't have to always do `databaseService.getApiKey().isNotEmpty()`.
