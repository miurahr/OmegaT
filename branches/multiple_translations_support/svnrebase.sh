#!/bin/bash
 
# usage:
# svnrebase [WORKING_COPY_PATH]
 
# set working copy path
WCPATH=${1-'.'}
 
cd $WCPATH
 
# read base URL
BASE_URL=`cat BASE_URL`
if [ "" == "$BASE_URL" ]; then
    echo "can't find base URL"
    exit
fi
 
# read current base revision
OLD_BASE_REV=`cat BASE_REV`
if [ "" == "$OLD_BASE_REV" ]; then
    echo "can't find current base revision"
    exit
fi
 
# fetch latest base revision
NEW_BASE_REV=`svn info $BASE_URL | grep 'Revision:' | cut -d ' ' -f 2`
if [ "" == "$NEW_BASE_REV" ]; then
    echo "can't find base"
    exit
fi
 
if [ "$OLD_BASE_REV" == "$NEW_BASE_REV" ]; then
    echo 'base already at latest revision'
    exit
fi
 
# apply changes to fork
svn merge $BASE_URL@$OLD_BASE_REV $BASE_URL@$NEW_BASE_REV
 
# update current base revision
echo $NEW_BASE_REV > BASE_REV
 
echo updated base to revision $NEW_BASE_REV
