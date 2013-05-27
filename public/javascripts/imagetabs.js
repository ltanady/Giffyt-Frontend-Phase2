/*

Image Navigation Menu Tabs v 1.0
Created on 11th December 2011
By M A Rafee

Special and all Credits goes to 

jQuery Foundation / Developers
Experts on Stackoverflow.com
My Colleques [ who were looking for this type of navigation image tab system, but couldn't. At last.. We phew.. they got it. ]
And Others.

	- Rafee : marafee1243@gmail.com
	- username : user@domain.com

// ----------------------------------------------------------------------------------- //

	Functionality
	---------------
	
	We have lot of Navigation menu tabs in jquery / css.
	
	But this one is entirely on Image Menu Tabs
	
	Here each and every navigation menu has image respectively.
	When hover on menu, displays the hovering image
	when clicked on menu, displays the clicked image
	
	
	How to use it
	---------------
	There are 3 images for one Menu Tab
		ie 	normal	: its the normal image
			hover	: its the image when you hover on normal image
			clicked	: its the image when you clicked on normal / hover image
			
	For example
	
	We have 3 Navigation Menu  Tabs 
	
	MenuOne, MenuTwo, MenuThree
	
	We need to rename the images with -n , -h, -c respective functionality below
	
	
	Functionality	|	MenuOne	|	MenuTwo		|	MenuThree 	|
	-------------------------------------------------------------
					|			|				|				|
	at Normal 		|	a-n.png	|	b-n.png		|	c-n.png		|
					|			|				|				|
	on Hover 		|	a-h.png	|	b-h.png		|	c-h.png		|
					|			|				|				|
	at Active		|	a-c.png	|	b-c.png		|	c-c.png		|
					|			|				|				|
	-------------------------------------------------------------
	
	Legends 
			-n : for Normal Image
			-h : for Hovering Image
			-c : for Clicked or Active Image.
			
			Just rename the image before executing 
	
			
	


// ----------------------------------------------------------------------------------- //

I love OpenSource, 
You share it,
You enjoy it.


Feel free to implement new ideas, upgrade it and share it.

NOTE : 

1. Use it at your own risk, any damages or loss, no responsiblities taken.
2. If you are upgrading please upload whole directory including jquery files and images in zip file.
3. Please do mention your name and email, at username: user@domain.com below credits.
4. Documentation is provided where ever it is necessary.


*/



	$(document).ready(function(){
		
		
		/* hiding all new elements starts here */
		
		$(".TabContent .content").hide();		// hiding all content elements
		$(".TabContent .content:eq(0)").show(); // then displaying only first content element as default
		
		/* hiding all new elements ends here */
		
				
				
		/* When image is clicked, code starts here */
		$(".image_tab li img").click(function(){
			
			var img_path = $(this).attr('src');
			
			var hasclass1 = $(this).parent().hasClass('active_class'); // get the class of `li` .
			
			if(hasclass1 > 0) {
				//do not write any thing here
			}
			else {
				
				$("img").each(function(){
					$(this).attr('src', $(this).attr('src').replace('-c.','-n.'));
				});
				
				
				var myRegExp1 = /-h./;
				var myRegExp2 = /-h-h./;
				var myRegExp3 = /-c./;
				
				var match1 = img_path.search(myRegExp1);
				var match2 = img_path.search(myRegExp2);
				
				if(match1 != -1) {
					
					$(this).attr('src', $(this).attr('src').replace('-h.','-c.'));
					$(".image_tab li").removeClass('active_class');
					$(this).parent().addClass('active_class');
					
					/* Change the way you want to hide and show the images in transtion starts here */
					/* if you want to speed up or speed down the hiding effects just add 
					
						.hide(1000);
						.show(1000);
					*/
					
					var p1 = $(".image_tab li img").index(this);
					
					$(".TabContent .content", '1').hide();
										
					$(".TabContent .content").hide();
					var pcontent = $(".TabContent .content:eq(" + p1 + ")").show();

					/* Change the way you want to hide and show the images in transtion ENDs here */					
					
					
					
					
				}
				
				if(match2 != -1) {
					$(this).attr('src', $(this).attr('src').replace('-h-h.','-c.'));
					$(".image_tab li").removeClass('active_class');
					$(this).parent().addClass('active_class');
				}
			
			}
			
		});
		/* When image is clicked, code starts here */
		
		/* mouser over function starts here */
		$(".image_tab li img").mouseover(function(){
			var img_path = $(this).attr('src');
			//alert(img_path);
			
			var myRegExp1 = /-n./;
			var myRegExp2 = /-c./;
			
			var match1 = img_path.search(myRegExp1);
			var match2 = img_path.search(myRegExp2);
			
			var hasclass1 = $(this).parent().hasClass('active_class');
			
			if(hasclass1 > 0) {
			
			}
			else {
			
				if(match1 != -1) {
					$(this).attr('src', $(this).attr('src').replace('-n.','-h.'));
				}
				
				if(match2 != -1) {
					$(this).attr('src', $(this).attr('src').replace('-c.','-h.'));
				}
			
			}
		});
		/* mouser over function ends here */
		
		/* mouse out function starts here */
		$(".image_tab li img").mouseout(function(){

			var img_path = $(this).attr('src');
			
			var myRegExp1 = /-h./;
			var myRegExp2 = /-c./;
			
			var match1 = img_path.search(myRegExp1);
			var match2 = img_path.search(myRegExp2);
			
			var hasclass3 = $(this).parent().hasClass('active_class');
			
			if(hasclass3 > 0) {
				//do not do anything here
			}
			else {
				if(match1 != -1) {
					$(this).attr('src', $(this).attr('src').replace('-h.','-n.'));
				}
				
				if(match2 != -1) {
					//$(this).attr('src', $(this).attr('src').replace('-c.','-c.'));
				}
			
			}
			
		});
		/* mouse out function ends here */
		
		
	});
	/* End of Code */
	