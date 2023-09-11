import groovy.json.JsonSlurper

def getMachineInfo() {
    // PowerShell command to retrieve hostname, IP address, and other machine information
    def script = '''
        $hostname = hostname
        Write-Host "Hostname: $hostname"
        
        # PowerShell command to retrieve IPv4 IP address
        $IP = (Get-NetIPAddress | Where-Object { $_.AddressFamily -eq "IPv4" }).IPAddress
        Write-Host "IP Address: $IP"
        
        # PowerShell command to get the current time in IST
        $currentTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        Write-Host "Time: $currentTime"
        
        # PowerShell command to retrieve last boot time in UTC
        $lastBootTimeUTC = Get-WmiObject Win32_OperatingSystem | Select-Object LastBootUpTime
        $lastBootTimeIST = [System.Management.ManagementDateTimeConverter]::ToDateTime($lastBootTimeUTC.LastBootUpTime).ToString("yy-MM-dd HH:mm:ss")
        Write-Host "Last Boot Time (IST): $lastBootTimeIST"
        
        # PowerShell command to retrieve drive details
        $DrivesPresent = Get-WmiObject Win32_LogicalDisk | ForEach-Object {
            "Drive: $($_.DeviceID) - Size: $([math]::Round($_.Size / 1GB, 2)) GB - Free Space: $([math]::Round($_.FreeSpace / 1GB, 2)) GB"
        }
        Write-Host "Drives present in the Machine: $DrivesPresent"
        
        # PowerShell command to retrieve Virtual Processor (CPU)
        $cpuUtilization = Get-WmiObject Win32_PerfFormattedData_PerfOS_Processor | Select-Object PercentProcessorTime
        $virtualProcessorInfo = Get-WmiObject Win32_ComputerSystem | Select-Object NumberOfLogicalProcessors, NumberOfProcessors
        Write-Host "Virtual Processors(CPU): " + $($virtualProcessorInfo.NumberOfLogicalProcessors) + " (Total: " + $($virtualProcessorInfo.NumberOfProcessors) + ")"
        
        # PowerShell command to retrieve CPU utilization
        $Processor = (Get-WmiObject -Class win32_processor -ErrorAction Stop | Measure-Object -Property LoadPercentage -Average | Select-Object Average).Average
        Write-Host "CPU Utilized: $Processor %"
        
        # PowerShell command to retrieve memory information
        $memory = Get-WmiObject Win32_ComputerSystem | Select-Object TotalPhysicalMemory
        $totalMemoryGB = [math]::Round($memory.TotalPhysicalMemory / 1GB, 2)
        Write-Host "Total Memory: $totalMemoryGB GB"
        
        # PowerShell command to retrieve memory utilization
        $ComputerMemory = Get-WmiObject -Class win32_operatingsystem -ErrorAction Stop
        $Memory = ((($ComputerMemory.TotalVisibleMemorySize - $ComputerMemory.FreePhysicalMemory) * 100) / $ComputerMemory.TotalVisibleMemorySize)
        $RoundMemory = [math]::Round($Memory, 2)
        Write-Host "Memory Utilized in %: $RoundMemory %"
        
        # PowerShell command to retrieve the last installed patch
        $lastInstalledPatch = Get-WmiObject Win32_QuickFixEngineering | Sort-Object InstalledOn -Descending | Select-Object -First 1
        Write-Host "Recent patch installed:"
        Write-Host "  HotFixID: $($lastInstalledPatch.HotFixID)"
        Write-Host "  Description: $($lastInstalledPatch.Description)"
        Write-Host "  Installed On: $($lastInstalledPatch.InstalledOn)"
        
        return @{
            "Hostname" = $hostname
            "IP Address" = $IP
            "Time" = $currentTime
            "Last Boot Time (IST)" = $lastBootTimeIST
            "Virtual Processors(CPU)" = "$($virtualProcessorInfo.NumberOfLogicalProcessors) (Total: $($virtualProcessorInfo.NumberOfProcessors))"
            "CPU Utilized" = "$Processor %"
            "Total Memory" = "$totalMemoryGB GB"
            "Memory Utilized" = "$RoundMemory %"
            "Recent patch installed" = "$($lastInstalledPatch.HotFixID) - $($lastInstalledPatch.Description) - $($lastInstalledPatch.InstalledOn)"
            "Drives present in the Machine" = $DrivesPresent
        } | ConvertTo-Json
    '''
    return powershell(returnStdout: true, script: script).trim() // Trim any leading/trailing whitespace
}

pipeline {
    agent {
        label 'build'
    }
    stages {
        stage('Run on Agent 1') {
            agent {
                label 'Machine2'
            }
            steps {
                node('Machine2') {
                    script {
                        def machineInfo = getMachineInfo()
                        echo machineInfo
                        writeFile file: "machine_info_Machine2.json", text: machineInfo
                        stash name: 'machine_info_Machine2', includes: 'machine_info_Machine2.json'
                    }
                }
            }
        }
        stage('Run on Agent 2') {
            agent {
                label 'Machine3'
            }
            steps {
                node('Machine3') {
                    script {
                        def machineInfo = getMachineInfo()
                        echo machineInfo
                        writeFile file: "machine_info_Machine3.json", text: machineInfo
                        stash name: 'machine_info_Machine3', includes: 'machine_info_Machine3.json'
                    }
                }
            }
        }
        stage('Copy JSON Files to Master') {
            steps {
                unstash 'machine_info_Machine2'
                unstash 'machine_info_Machine3'
            }
        }
        stage('Create JSON File') {
            steps {
                script {
                    def machine1Json = readFile "machine_info_Machine2.json"
                    def machine2Json = readFile "machine_info_Machine3.json"

                    def jsonSlurper = new JsonSlurper()
                    def machine1Data = jsonSlurper.parseText(machine1Json)
                    def machine2Data = jsonSlurper.parseText(machine2Json)

                    def combinedJson = [
                        "Machine1": machine1Data,
                        "Machine2": machine2Data
                    ]
                }
            }
        }
    }
    post {         
        success {             
            // Trigger GitPipeline on successful build of Test             
            build job: 'GitPipeline'
        }
    }
}
