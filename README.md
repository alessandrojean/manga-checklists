# Mangas Checklist

Simple app to view the monthly checklists from the brazilian manga publishers.

Unfortunately, the publishers doesn't provide an proper API, 
so the app just crawl the data from their websites, by simple
requests, and show the content.

The possibility of a creation of an proper server dedicated to 
parse the data exists, but it's not the main purpose.

## Publishers

Available at moment:
- [Editora JBC](https://mangasjbc.com.br/): checklists, plans and details when available;
- [Panini Comics](https://loja.panini.com.br/): checklists and details when available;
- [NewPOP Editora](http://www.newpop.com.br): checklists and details when available.

## Screenshots

![Screenshots](https://user-images.githubusercontent.com/14254807/34279885-f135777e-e69a-11e7-9fc2-5b61e71c29b8.png)

## How it works?

The app obtain the data from the publishers website, in HTML format, 
and uses a parser, in this case Jsoup, to get the important informations.
 
This method it's not good since that any layout changes in the publishers
website can cause an instantaneous crash in the app.

## License

    The MIT License (MIT)

    Copyright (c) 2017 Alessandro Jean

    Permission is hereby granted, free of charge, to any person obtaining a copy of
    this software and associated documentation files (the "Software"), to deal in
    the Software without restriction, including without limitation the rights to
    use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
    the Software, and to permit persons to whom the Software is furnished to do so,
    subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
    FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
    COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
    IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
    CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
    
## Disclaimer

The developer it's not filiated with any of the publishers, the app just uses
the public data available in their websites.