use strict;
use warnings;
use File::Slurp;
use File::Basename;
use MP3::Info;

my $file = $ARGV[0];
my $style = $ARGV[1];
my $name = basename ($file, '.txt');
my $index = $ARGV[2];

my @lines = read_file ($file);
my $n_sentences = @lines;

my $counter = 1;
foreach (@lines) {
	chomp $_;
	my $sentence = $_;
	my $outfile = sprintf("%03d_%s.html", $counter+$index, $name);	
	my $audiofile = sprintf("%03d_%s.mp3", $counter+$index, $name);		
	my $audioinfo = get_mp3info($audiofile);	
	my $audiolen = $audioinfo->{SECS};
	my $audiolenms = $audiolen * 1000.0;
	print $audiolenms . "\n";
	my $nextfile = sprintf("../questions/%03d_%s.html", $counter+$index+1, "question");	
	open (my $fp, '>', $outfile);	
	print $fp "<html>\n<body style=\"height:100%;width:100%;display:table\">\n<!--audio id=\"myAudio\" controls autoplay hidden=\"hidden\" preload=\"auto\">\n\t<source src=\"" . $audiofile . "\" type=\"audio/mpeg\" />\n</audio-->\n<script>\n\tsetTimeout(function(){window.location.href=\"" . $nextfile . "\"}, " . $audiolenms . ");\n</script>\n<div align=\"center\" style=\"display:table-cell;text-align:center;vertical-align:middle\">\n\t<img width=\"300\" src=\"alice.png\">\n\t<h1 style=\"" . $style . "\">" . $sentence . "</h1>\n\t<small>" . $name . " (" . $counter . "/" . $n_sentences . ")</small>\n</div>\n</body>\n</html>";
	$counter++;
	close ($fp);
}