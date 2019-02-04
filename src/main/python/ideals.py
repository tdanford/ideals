#!/usr/bin/env python3

"""Wrappers and helper scripts for invoking the Java Ideals code
"""

import shutil
import subprocess
import os
import re
import pathlib
import sys

class Jar:
    """Wraps a Java Jar file and allows easy invocation
    """
    def __init__(self, jar_path):
        """Wrap a jar accessible at :jar_path:
        """
        assert isinstance(jar_path, pathlib.Path), '%s should be a Path' % type(jar_path)
        self.path = jar_path

    def invoke(self, *args):
        cmd_line = ['java', '-jar', self.path.as_posix()] + list(args) 
        print('Running: %s' % str(cmd_line))
        subprocess.run(cmd_line)

def find(iname, path=None):
    assert path is None or isinstance(path, pathlib.Path), '%s should be a Path' % type(path)
    dir = path.as_posix() if path is not None else '.'
    cmdline = ['find', dir, '-iname', iname]
    p = subprocess.run(cmdline, stdout=subprocess.PIPE)
    output = p.stdout.decode('utf-8').strip().split('\n')
    return output
        
class Ideals:
    def __init__(self, ideals_path):
        self.ideals_base_path = ideals_path
        jar_paths = find('ideals*jar', path=self.ideals_base_path)
        if len(jar_paths) == 0:
            raise ValueError('No ideals jar in %s' % self.ideals_base_path.as_posix())
        self.jar_path = jar_paths[0]
        self.jar = Jar(pathlib.Path(self.jar_path))
    def visualization(self, poly_string):
        self.jar.invoke(poly_string)

def main(args):
    base_path = pathlib.Path(args[0])
    i = Ideals(base_path)
    i.visualization(args[1])

if __name__=='__main__':
    main(sys.argv[1:])

        
