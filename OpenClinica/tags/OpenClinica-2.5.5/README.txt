--------------------------------------------------------------
OPENCLINICA RELEASE NOTES
OpenClinica Version 2.5.5
Document Version 1.0
--------------------------------------------------------------

--------------------------------------------------------------
Updated: April 3, 2009 Paul Galvin pgalvin@akazaresearch.com

--------------------------------------------------------------

--------------------------------------------------------------
OVERVIEW
--------------------------------------------------------------

This release of OpenClinica addresses issues found with dates in rule expressions when the browser is set to use the European date format and users who have a oc_date_format_string of dd/MM/yyyy.  The oc_date_format_string affects users who entered data when OpenClinica was set to use only French, Spanish, or Italian.

To download a sample database to install with OpenClinica for demonstration purposes only, please go to the same page you downloaded this package from.  

--------------------------------------------------------------
CONTENTS OF THIS DOCUMENT
--------------------------------------------------------------
 
I.   OVERALL PRODUCT FEATURES
II.  ISSUES ADDRESSED AND KNOWN ISSUES
III. SOFTWARE DEPENDENCIES AND SYSTEM REQUIREMENTS
IV.  ABOUT OPENCLINICA
V.   GNU LGPL LICENSE

--------------------------------------------------------------
I. OVERALL PRODUCT FEATURES
--------------------------------------------------------------

Overall Product Features

The main modules include:
* Submit Data: Allows subject enrollment, data submission and validation for use by clinicians and research associates as well as Query Management and Bulk Data Import.
* Extract Data: Enables data extraction and filtering of datasets for use by investigators and principal investigators.
* Manage Study: Facilitates creation and management of studies (protocols), sites, CRFs, users and study event definitions by principal investigators and coordinators.
* Business Admin/Technical Admin: Allows overall system oversight, auditing, configuration, and reporting by administrators.

Some key features of OpenClinica include: 

* Organization of clinical research by study protocol and site, each with its own set of authorized users, subjects, study event definitions, and CRFs. Support for sharing resources across studies in a secure and transparent manner. 
* Dynamic generation of web-based CRFs for electronic data capture via user-defined clinical parameters and validation logic specified in portable Excel templates.
* Management of longitudinal data for complex and recurring patient visits.
* Data import/export tools for migration of clinical datasets in excel spreadsheets, local databases and legacy data formats.
* Extensive interfaces for data query and retrieval, across subjects, time, and clinical parameters, with dataset export in common statistical analysis formats. 
* Compliance with HIPAA privacy and security guidelines including use of study-specific user roles and privileges, SSL encryption, de-identification of Protected Health Information (PHI), and auditing to monitor access and changes by users.
* A robust and scalable technology infrastructure developed using the Java J2EE framework interoperable with relational databases including PostgreSQL (open source) and Oracle 10G, to support the needs of the clinical research enterprise. 



--------------------------------------------------------------
II. ISSUES ADDRESSED AND KNOWN ISSUES
--------------------------------------------------------------
  A.  If the user�s browser setting is set to something other than EN or EN-US, a faulty edit check will fire if a rule with a date has been applied to a data field.  If the web browser is not using en or en-us, the day and month will be flipped when the rule is executing.  This will cause a faulty edit check.
     
    * Example:  A rule is written to say the date can not be after than July 7th, 2007.  The user enters a date in the European date format of 08/05/2007 (May 5th, 2007).  The rule will flip the date to 05/08/2007 which means the system interprets it as August 5th, 2007, which will lead the edit check to fire.


  B. If a data entry person has set their language in the web browser to use ES, FR, or IT, dates will be stored in the database incorrectly.  A date property that should only be set to MM/dd/yyyy was set to dd/MM/yyyy in the ES, FR, and IT properties files.  This causes the date to be stored incorrectly in the database and could adversely affect users who want to write rules involving dates.  Rules depends on the dates being stored in the database in the MM/dd/yyyy format.
    * A script has been provided for users that will flip the day and month of the date in the database for items with a datatype of date. 
    * The property that was changed has been removed from the property file and has been hard-coded into the application. 

Known issues can be found at http://www.openclinica.org/dokuwiki/doku.php?id=ocknownissues:start

--------------------------------------------------------------
III. SOFTWARE DEPENDENCIES AND SYSTEM REQUIREMENTS
--------------------------------------------------------------

Pre-requisites (versions):
    Operating system(s): Windows XP, Windows Server 2003, Redhat Enterprise Linux 4.0+
    Browsers: Internet Explorer 6.0 or 7.0, Mozilla Firefox 1.5 or 2.0
    JDK : 1.5.0_16
    Application server : Tomcat 5.5.27
    Database server : PostgreSQL 8.2.9 or Oracle 10g
    OpenClinica version for Upgrades only: OpenClinica 2.2.1 or 2.2.2

The source code has been removed from the distribution package to make it easier to navigate the file structure.  To access the source code, please follow the instructions at http://www.openclinica.org/dokuwiki/doku.php?id=developerwiki:start#developing_with_openclinica.

NOTE: You must configure the smtp host correctly, or the web page may time out when trying to send emails. 

--------------------------------------------------------------
IV. ABOUT OPENCLINICA
--------------------------------------------------------------

OpenClinica: Open Source Software Platform for Managing Clinical Research Studies Professional Open Source Solutions for the Clinical Research Enterprise

OpenClinica is a free, open source clinical trial software platform for Electronic Data Capture (EDC) clinical data management in clinical research. The software is web-based and designed to support all types of clinical studies in diverse research settings. From the ground up, OpenClinica is built on leading independent standards to achieve high levels of interoperability. Its modular architecture and transparent, collaborative development model offer outstanding flexibility while supporting a robust, enterprise-quality solution.

More about OpenClinica: http://www.OpenClinica.org

Software License

OpenClinica is distributed under the GNU Lesser General Public License (GNU LGPL). For details see: http://www.openclinica.org/license or LICENSE.txt distributed with this distribution.

Developer and Contact Information

Akaza Research, based in Cambridge, MA, provides clinical trials informatics solutions based on OpenClinica, the world's most widely used open source clinical trials software.

Akaza Research
One Kendall Square
Bldg. 400, 4th Floor
Cambridge, MA 02139
phone: 617.621.8585
fax: 617.621.0065
email: contact@akazaresearch.com

For more about Akaza�s products and services see:
http://www.akazaresearch.com/
and
http://www.OpenClinica.org/ 


--------------------------------------------------------------
V. GNU LGPL LICENSE
--------------------------------------------------------------

OpenClinica is distributed under the GNU Lesser General Public License (GNU LGPL), 
summarized in the Creative Commons text here:

http://creativecommons.org/licenses/LGPL/2.1/

The GNU Lesser General Public License is a Free Software license. Like any Free Software
license, it grants to you the four following freedoms:

0. The freedom to run the program for any purpose.
1. The freedom to study how the program works and adapt it to your needs.
2. The freedom to redistribute copies so you can help your neighbor.
3. The freedom to improve the program and release your improvements to the public, so 
that the whole community benefits.

You may exercise the freedoms specified here provided that you comply with the express conditions of this license. The LGPL is intended for software libraries, rather than executable programs.

The principal conditions are:

* You must conspicuously and appropriately publish on each copy distributed an appropriate copyright notice and disclaimer of warranty and keep intact all the notices that refer to this License and to the absence of any warranty; and give any other recipients of the Program a copy of the GNU Lesser General Public License along with the Program. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you modify your copy or copies of the library or any portion of it, you may distribute the resulting library provided you do so under the GNU Lesser General Public License. However, programs that link to the library may be licensed under terms of your choice, so long as the library itself can be changed. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you copy or distribute the library, you must accompany it with the complete corresponding machine-readable source code or with a written offer, valid for at least three years, to furnish the complete corresponding machine-readable source code. You need not provide source code to programs which link to the library.

Any of these conditions can be waived if you get permission from the copyright holder.
Your fair use and other rights are in no way affected by the above.

For the full GNU LGPL License text, see LICENSE.txt included in this package.

