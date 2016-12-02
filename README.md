# vndb
Calculates the cosine similarity of two visual novels or two visual novel characters using data obtained from vndb

What is Cosine Similarity? 

https://en.wikipedia.org/wiki/Cosine_similarity

https://en.wikipedia.org/wiki/Tf-idf

Tf-idf cause more uncommon tags to have a higher weight when calculating the cosine similarity of two visual novels.

That means that very common tags for a visual novel like "Romance" have much less of a weight than rare tags like "Voiced Protagonist".

You can download the tags and traits data files needed for this program to run from https://vndb.org/d14.

You will also need to download cached character and visual novel data files from http://uppit.com/qhfhfpq4bjg4 and http://uppit.com/geo47ymuc3p2, or download them yourself using the vndb API.

Place the tags and traits files in the project folder and extract Chars.rar and Data.rar into the project folder.

Example output:

```
Received: ok
Using cached data for: 5834
Searched : 15680 visual novels.
Most similar visual novels to: Irotoridori no Sekai (5834)
Irotoridori no Hikari (10028): 0.6903076860624636
Akai Hitomi ni Utsuru Sekai (17147): 0.33733452804823133
Himawari to Koi no Kioku (14926): 0.33192738928419013
Hime-sama, Ote Yawaraka ni! (2829): 0.302443519931272
Suzukaze no Melt -Where Wishes are Drawn to Each Other- (3992): 0.29582596565951663
Hoshizora no Memoria -Wish upon a Shooting Star- (1474): 0.28592872885951404
Sakura Strasse (205): 0.28401042969859447
Amnesia World (13804): 0.25943195350978776
Dekinai Watashi ga, Kurikaesu. (15166): 0.2590816310355958
Ashita no Kimi to Au Tame ni (423): 0.25334322734191383
Time elapsed: 1370ms

Process finished with exit code 0
```
