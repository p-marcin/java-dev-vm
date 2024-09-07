# Certificate Setup

## :pushpin: Persisting changes

You'll need to mount two additional volumes in [restart.bat](../batch-scripts/restart.bat):
* `--mount source=ca-certificates,target=/usr/local/share/ca-certificates ^`
* `--mount source=certs,target=/etc/ssl/certs ^`

## :pushpin: Download Certificate

On a website click the padlock icon in browser's address bar and find [root certificate](https://en.wikipedia.org/wiki/Root_certificate):
   * Download it as `.crt` file. **The certificate must be in valid [PEM](https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail) format and have a `.crt` extension. Otherwise, it will not be processed**
   * If you downloaded it as `.pem` file, then you need to convert it to `.crt` file with command: `openssl x509 -in certificate.pem -out certificate.crt`
   * If you downloaded it as `.der` file (in [DER](https://en.wikipedia.org/wiki/X.690#DER_encoding) format), then you need to convert it to `.crt` file with command: `openssl x509 -in certificate.der -inform DER -out certificate.crt -outform PEM`
   * If you downloaded it as `.p7c` file (as [PKCS#7](https://en.wikipedia.org/wiki/PKCS_7) in [DER](https://en.wikipedia.org/wiki/X.690#DER_encoding) format), then you need to convert it to `.crt` file with command: `openssl pkcs7 -in certificate.p7c -inform DER -print_certs -out certificate.crt`

## :pushpin: Ubuntu DEV VM

1. Copy `.crt` certificate to: `/usr/local/share/ca-certificates/`
2. Run: `sudo update-ca-certificates`
3. Check if a symbolic link to the certificate has been created with command: `ls -l /etc/ssl/certs | grep certificate.crt`
4. Check if the certificate was added to trusted certificates with command: `grep -f /usr/local/share/ca-certificates/certificate.crt /etc/ssl/certs/ca-certificates.crt`
5. Test: `curl --head -v https://site.com`

## :pushpin: CentOS (deprecated)

1. Copy `.crt` certificate to: `/etc/pki/ca-trust/source/anchors/`
2. Run: `sudo update-ca-trust`
3. Check if the certificate is present in one of the subdirectories of `/etc/pki/ca-trust/extracted/`
4. Check if the certificate was added to trusted certificates with command: `grep -f /etc/pki/ca-trust/source/anchors/certificate.crt /etc/pki/tls/certs/ca-bundle.crt`
5. Test: `curl --head -v https://site.com`
