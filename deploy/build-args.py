import sys
res=" --build-arg "
with open(sys.argv[1]) as f:
    res += " --build-arg ".join(line.strip() for line in f)
print (res)
sys.exit(0)