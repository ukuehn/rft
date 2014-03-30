RFT README
===

RFT is the Report Fiddling Toolkit designed to help analyse and process
certain reports given in raw data format. This input data is on single
report items and needs to be aggregated in order to be useful.

Installation
---

Type "ant".


Normal usage
---

To analyse raw data reports run

	java -jar rft.jar command options file

where command is one of

        csvparse        parse a CSV file.

        ori             process three-column report of item,id,value.

        patches         process XML patch report on single computers,
                        listing all relevant patches and their install
                        status. Multiple input files are supported.

        combpat         process CSV pattern report where pattern and date
                        are given in the combined column #11.

        seppat          process CSV pattern report where pattern and its date
                        are in separate columns #7 and #9. Additionally,
                        dates older than 32 days are cut off.

Options
---

  -q         suppress header output

  -d delim   use delim as csv field delimiter

  -r refdate set reference date to determine age, with allowed formats being
             yyyyMMdd, yyyy-MM-dd, or dd.MM.yyyy.

  -u         for command patches, include unknown state in output report

  -H s0:...:sn for command patches, give split values for histogram

  -O file    for command patches, send detailed output to file instead of
             standard output



Credits
---

RFT is developed by Ulrich Kuehn (ukuehn AT acm.org) and is released under
the GPL v2 or later.

