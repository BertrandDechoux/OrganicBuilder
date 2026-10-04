# Prompts Log

## 26-10-02
The Readme does not quite work for Linux — add Windows and Linux paragraphs.

## 26-10-02
the app runs but is unbearably small, nothing is readable. Adapt font size and all elements to the current screen resolution.

## 26-10-02 03:06
very good, now the resulting button "(a0+c0=>a1c1)" is too short, so the character string cannot be fully read in the interface. Make it wider.

## 26-10-02 03:20
Copilot review on PR #12 requested fixes: keep the early maximized-state request, prefer the primary output's DPI over the maximum, replace the unreliable pixel-height fallback with physical sizes from connector EDID, and use the concrete jar filename in the README for Windows compatibility.

## 26-10-03 01:14
In Organic Builder a screenshot must be added to the readme.

## 26-10-03 01:20
the java app starts in fullscreen mode; when you start moving it, it shrinks to 1px x 60px, which is way too small. you can drag it bigger, but at first it looks like a bug since it is just a line.

## 26-10-03 01:30
commit everything, then change the whole design to a more modern design.

## 26-10-03 01:37
new screenshot in the readme.

## 26-10-04
swap the buttons for modern pause play reset next previous ... and a help icon top right. also add more themes, selectable in a system menu. if possible create one theme that looks exactly like evoloom.

## 26-10-04
the "system" menu must be called "options", the help button in the top right can be removed, instead a help menu in the window bar next to Options with an Info item. the link to the repo in the popup must be clickable.

the themes flat light, dark light should be removed from the menu but stay in the code for later.

evoloom must be completely changed with the borders like evoloom, see screenshot. this is how evoloom should look.

## 26-10-04 01:10
is the app already multilingual? if yes, add a language selection to the options.

## 26-10-04 01:25
when I run mvn clean test: ActionConfigurerIntegrationTest throws "Uncaught error fetching image" NullPointerException (URLImageSource, url is null).

## 26-10-04 08:15
there are lots of encoding errors in the French, except for the explanation text "Bonjour. Pour utiliser l'éditeu..." everything is wrongly encoded in the menu and the popup message for restart.

## 26-10-04 08:40
in the theme selection only flat intellij and evoloom are shown, but selecting evoloom gives dark intellij I think. add dark-intellij back to the menu — they should still be in the code, just reactivate in the menu, name them correctly and in the right order.

## 26-10-04 11:30
menu.theme.intellij must be called Light; menu.theme.light must also be called light in english, properly translated in french; menu.theme.darcula = Dark, menu.theme.evoloom must also be called dark, translated into french.

