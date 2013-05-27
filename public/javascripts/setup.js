//SETUP//


var containerStat = 0		  //Container visible prior to images are loaded on/off
var toggleSpeed = 1000        //Dropdown speed - possible values: slow,normal,fast or xxxx milliseconds 
var percentComplete = "80%"   //% complete - sets bar location and bar % display
var barSpeed = 1500           //Bar animation speed in milliseconds
var animationDelay =800	  	  //Bar and tip start animation delay in milliseconds
var fadeSpeedIcons = "fast"   //Social icons hover speed - possible values: slow,normal,fast or xxxx milliseconds
var Year =  2012			  //Set the year
var Month  = 12			  	  //Set the month
var Day = 24				  //Set the day
var Hour = 15				  //Set the hour of the day
var Min = 5                   //Set the Min
var Sec = 2                   //Set the Sec

//Social network ids - only fill out the ids, not the full url
var facebookPageID ="UnlimitDesign/167541586629542"
var twitterID = "udfrance"
var myspaceID = "ironmaiden"
var skypeID ="udfrance"

//Contact from messages
var formBorderVerify = '1px solid #d95880'  //width, type, color can be changed
var formError="There was an error sending your email. Please try again."
var formWarning ="Verify fields, and try again!"
var formSuccess ="Thanks, we got your mail and will get back to you in 48h!"
var formSuccessTitle ="Message sent"
var formReload ="Send us a mail and we will get back to you in 48 hours."
var formReloadTitle ="Got something to say..."

//Notify field messages
var notifyError ="Sorry, an error occurred, please try again"
var notifyWarning ="Invalid e-mail, try again!"
var notifySuccess ="E-mail added, you'll be notified when we launch!"


//SUPERSIZE VARIABLE
jQuery(function($){
				$.supersized({
				
//Functionality

slideshow               :   1,		//Slideshow on/off
autoplay				:	1,		//Slideshow starts playing automatically
start_slide             :   1,		//Start slide (0 is random)
random					: 	0,		//Randomize slide order (Ignores start slide)
slide_interval          :   3000,	//Length between transitions
transition              :   1, 		//0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
transition_speed		:	1000,	//Speed of transition
performance				:	1,		//0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)
image_protect			:	1,		//Disables image dragging and right click with Javascript
keyboard_nav            :   0,		//Keyboard navigation on/off

				
slides 					:  	[		//Slideshow Images
							{image : 'images/background.jpg', title : 'Your caption here'},  
							{image : 'images/background.jpg', title : 'Your caption here'},  
							{image : 'images/background.jpg', title : 'Your caption here'}, 
							{image : 'images/background.jpg', title : 'Your caption here'}   
							]
												
	}); 
});