use strict;
use warnings;
use File::Slurp;
use File::Basename;

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
	open (my $fp, '>', $outfile);	
	print $fp "<html>\n<body style=\"height:100%;width:100%;display:table\">\n<div align=\"center\" style=\"display:table-cell;text-align:center;vertical-align:middle\">\n\t<img width=\"300\" src=\"microphone.png\">\n\t<h1 style=\"" . $style . "\">" . $sentence . "</h1>\n\t<small>" . $name . " (" . $counter . "/" . $n_sentences . ")</small>\n</div>\n</body>\n</html>";
	$counter++;
	close ($fp);
}