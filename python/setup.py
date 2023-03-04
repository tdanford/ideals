#!/usr/bin/env python

from setuptools import setup, find_packages

setup(
    name="idealspy",
    version="0.1.0",
    description="Algebraic geometry examples in Python",
    url="https://www.github.com/tdanford/idealspy",
    packages=find_packages(),
    install_requires=[
        "click",
    ],
    tests_require=["pytest", "black", "pytest-black", "pytest-cov"],
)
