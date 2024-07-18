# API Documentation

<!-- TOC -->
* [API Documentation](#api-documentation)
  * [Pool API](#pool-api)
      * [Authorization](#authorization)
  * [Gate API](#gate-api)
      * [Business Partner](#business-partner)
      * [Address Type](#address-type)
      * [Stages](#stages)
      * [Sharing State](#sharing-state)
      * [Changelog](#changelog)
      * [Additional information](#additional-information)
    * [Authorization](#authorization-1)
    * [Sharing a new business partner](#sharing-a-new-business-partner)
      * [Sharing a Business Partner With Site Information](#sharing-a-business-partner-with-site-information)
    * [Update a Business Partner](#update-a-business-partner)
    * [Dummy Golden Record Process Restrictions](#dummy-golden-record-process-restrictions)
      * [Categorization](#categorization)
      * [Cleaning Data](#cleaning-data)
      * [Duplication Check](#duplication-check)
      * [Data Provisioning](#data-provisioning)
      * [High-Level Input Output Examples](#high-level-input-output-examples)
      * [High Level Golden Record Examples](#high-level-golden-record-examples)
  * [Orchestrator API](#orchestrator-api)
      * [Tasks](#tasks)
      * [Processing Steps](#processing-steps)
      * [Clean And Sync Step](#clean-and-sync-step)
  * [Access BPDM over EDC](#access-bpdm-over-edc)
  * [NOTICE](#notice)
<!-- TOC -->

Here you can find documentation on how to access and integrate BPDM APIs.
The main user groups for BPDM are sharing members, golden record processing service providers and VAS providers.

This document contains explanations for different use cases for these user groups.
Accompanied by these explanations is a [Postman collection](../postman/BPDM%20Tests.postman_collection.json) showcasing these use cases.
Please mind that the requests in this Postman collection are not meant to be executed for automated tests but rather serve as documentation.

## Pool API

With the [Pool API](pool.yaml) you can query golden record and available metadata information like legal forms and identifier types.
Value added services who operate on golden record data mainly use this API.
However, this API may also be interesting to sharing members who want to see which metadata information the golden record process provider supports.

Important concepts:

- Golden Record: a golden record is a business partner which has been processed by the golden record process and resides in the BPDM Pool.
  A golden record is categorized either as a legal entity, site or logistic address.
- Legal Entity: a legal entity is a golden record that only contains legal entity information and its legal address.
  It is referenced by a BPNL.
- Site: a site, i.e. a bigger compound that may contain several logistic addresses, is a golden record that contains the site information and its main address.
  It is referenced by a BPNS.
  A site belongs to exactly one legal entity.
  Its main address can be the legal address of the legal entity.
  A legal entity can have several sites but its legal address can only reside in one site.
- Logistic Address: a logistic address is a golden record that only contains address information.
It is referenced by a BPNA.
A logistic address can be a legal address and/or a site main address.
Each logistic address belongs to exactly one legal entity and up to one site.
- Metadata: Metadata extends golden record information, e.g. which legal forms and legal entity identifiers are available for legal entities.
The actual available metadata contents depend on the golden record service provider.
Golden records refer to metadata by technical keys.
Those technical keys can be queried by the Pool API.
- Members: A subset of golden records are member golden records.
These are golden records which belong to Catena-X members.
- Changelog: The changelog contains the events of when golden records have been added or changed.
It does not contain the information of what has changed in a golden record.


#### Authorization

The BPDM Pool API recognizes two user groups:

1. Catena-X Members who can read golden record member data and the metadata.
2. Admins who have full read and write access to the golden record and metadata.

Permissions:

| Resource              | Catena-X Member | Admin  |
|-----------------------|-----------------|--------|
| Member Golden Records | R | R & W  |
| Member Changelog      | R | R      |
| Metadata              | R | R & W  |
| Golden Records        | - | R & W  |


   
## Gate API

With the [Gate API](gate.yaml) you can share business partner data with the golden record process and query the results.
This API is important for sharing members and value added services who offer extended functionality for sharing members.

#### Business Partner

A business partner is a logistic address augmented by additional meta information like names, identifiers and state of operation.
  The business partner data may contain legal entity and/or site information.
  For example, the business partner's name may include the name of the legal entity the logistic address belongs to.
  Sharing member business partners are assigned up to three BPNs during the golden record process.
  A BPNA refers to the actual logistic address contained in the business partner data.
  A BPNL refers to the legal entity that logistic address belongs to.
  An optional BPNS exists if the logistic address also belongs to a site of that legal entity.

> Please note that a sharing member business partner is not a golden record even after going through the golden record process.
> It merely points to golden records via the attached BPNs and contains some data of golden record.

#### Address Type

A business partner has an address type indicating whether the address is a registered legal address, site main address or none of those.
For services who want to categorize sharing member business partners into golden record types you could therefore make these connections:

| Address Type                | Golden Record Type |
|-----------------------------|--------------------|
| Legal Address               | Legal Entity       |
| Site Main Address           | Site               |
| Legal And Site Main Address | Site\*             |
| Additional Address          | Logistic Address   |


\* Note that the Legal and Site Main Address type is special in this regard since it indicates a site that has the legal address as its site main address.
For each legal entity there can only be up to one of such sites.

> Categorizing this business partner data in such way should not mean that this business partner data is in fact the golden record - in fact it is not.
> While the business partner can contain data of a logistic address golden record it may also additionally contain legal entity and site information at the same time.

#### Stages

Business partners have two types of data: input and output data.
Input data is the version of the business partner how it is shared by the sharing member.
Output data is the version of the business partner after it has gone through in the golden record process.
Sharing members can only update the input data and the golden record process can only update the output data.

#### Sharing State

Each business partner has a sharing state in regard to the golden record process.
The sharing state indicates whether the business partner input data has not yet been shared, is currently in processing or finished.

> Initial Sharing State: The BPDM API can be configured to support an additional 'Initial' sharing state.
> Such a sharing state indicates that the business partner input data has been changed but is not yet marked for sharing.
> If that is the case the sharing state needs to be manually set to ready to be shared to the golden record process.
> If no initial sharing state has been configured uploaded input data is automatically shared to the golden record process as soon as it is received.


#### Changelog

Contains events for each stage on when business partner data has been added or changed.
The changelog only contains the information of when the business partner changed not what changed.


#### Additional information

The Gate API only works in context of one sharing member at a time.
This means, a sharing member or value added service may only see the business partner data information of one sharing member at a time.

The full implementation of the Golden Record Process - which includes duplication checks, categorizing and cleaning of data - is not yet provided in this repository.
Instead, BPDM offers a dummy golden record processing service that performs rudimentary checks and processing to offer a limited Golden Record Process without relying on an external provider.
Since the dummy service is very limited when using the BPDM API behind such a dummy golden record process comes with restrictions.
The next sections deal with how to use the BPDM API as a sharing member with a real golden record process and what to consider with the dummy service.


### Authorization

The BPDM Gate API considers the following user groups:

1. Input Consumer: can read business partner input data
2. Output Consumer: can read business partner output data
3. Input Manager: can read and write business partner input data and may also start the sharing process
4. Admin: full access to all resources

Permissions:

| Resource                | Input Consumer | Output Consumer | Input Manager | Admin |
|-------------------------|----------------|-----------------|---------------|-------|
| Business Partner Input  | R              | -               | R & W         | R & W |
| Business Partner Output | -              | R               | -             | R & W |
| Sharing State           | R              | R               | R & W         | R & W |
| Input Changelog         | R              | -               | R             | R     |
| Output Changelog        | -              | R               | -             | R     |
| Statistics              | R              | R               | R             | R     |

### Sharing a new business partner

Postman collection request ID: S.CP

The general idea for sharing business partner data is

1. for a sharing member to upsert business partner data to the input stage
2. set the data to be sharing ready (if Gate API not configured to automatically do that)
3. query the sharing state and wait until the golden record process finished
4. fetch the result in the output stage of the business partner

On uploading a business partner to the input stage, the sharing member assigns an `externalId` which he can use to identify the business partner across stages.
In fact the `externalId` is the main identifier for querying business partner data in the Gate.

Except the `externalId` there are no required properties for a business partner.
However, if the provided data is too few or of too bad quality the business partner might be rejected by the golden record process.
Querying the sharing state of the business partner will reveal the current state in the golden record process.
Here, the sharing member can also see whether and why the business partner has been rejected.


#### Sharing a Business Partner With Site Information

Postman collection request ID: S.CPS

Currently, the golden record process can not determine on its own whether a logistic address belongs to a site or not.
It is not able to extract site information on its own.

Additionally, the golden record process will only consider site information from business partners which are shared by the owners.
A sharing member is an owner of a business partner if the logistic address is part of the sharing member's company.

For these two reasons in order to share business partner data with site information, a sharing member needs to claim the business partner data by
setting `isOwnCompanyData` to true.
Also, for the golden record process to recognise the site information, the sharing member needs to set the `siteName` field.

If both conditions in the business partner data are fulfilled and the ownership claim is validated by the golden record process, a site golden record will be
created as part of the result.


### Update a Business Partner

Postman collection request ID: S.UP

There are two instances of when a sharing member might want to update their business partner input:

1. They want to overtake data from the output result
2. They want to share new information they gathered from other channels

In both cases the sharing member follows the same procedure by updating the business partner input and waiting for confirmation from the golden record process.
This way, sharing a new business partner and updating an existing business partner follows the same process.

Please be aware that when a sharing member updates a business partner and sends it to the golden record process, the output may have different BPNs from before.
This is because the process may determine that the business partner in fact describes different golden records than previously assumed.
The previous golden records remain in the Pool.

Sharing members may only update business partner input if the data is really different.
If the upserted input data is identical to the existing input data, neither a changelog entry is created nor can the data be given to the golden record process.

>Please note that you can enhance the accuracy of the golden record process by providing BPNs to the records you want to update.
>Therefore, it is strongly encouraged to always update inputs with the BPNs you received in the previous result.

### Dummy Golden Record Process Restrictions

A golden record process which is implemented by using the BPDM Cleaning Service Dummy has some unique restrictions.

#### Categorization

The dummy golden record process can not effectively categorize the shared business partner address and determine the golden records affected on its own.
Therefore, in order to reliably share business partner data which is known to be of a certain type - for example it is known that it contains a legal address - this information has to be provided with the business partner input.

The dummy service has the following behaviour for categorizing business partner data to be created or updated:

| Has Site Information | Address Type                  | Golden Record Result                           |
|----------------------|-------------------------------|------------------------------------------------|
| No                   | NULL                          | Legal Entity                                   |
| Yes                  | NULL                          | Site with legal address as site main address   |
| -                    | Legal Address                 | Legal Entity                                   |
| -                    | Legal And Site Main Address   | Site with legal address as site main address   |
| -                    | Site Main Address             | Site                                           |
| No                   | Additional Address            | Additional Address of Legal Entity             |
| Yes                  | Additional Address            | Additional Address of Site                     |


#### Cleaning Data

The dummy service does not clean or correct incorrect data.
This means typos or incorrect address data or names are not corrected.

This is especially important for references to metadata information in the Pool.
Legal Forms, identifier types and administrative areas need to be referenced by their technical key/ISO code as listed in the metadata Pool endpoints.

#### Duplication Check

The dummy service matches business partner data by BPN or name - if no BPN has been provided.
If a BPN has been provided the dummy service expects that the referenced golden record already exists and will fail to process the data if the golden record with that BPN can not be found.
If no BPN has been provided business partners are matched by name.
The name has to be case-sensitively exactly match.
If no business partner can be matched by name it will be created (only in case no BPN has been provided).

#### Data Provisioning


- Missing Parents: If the golden record process determines a record to be a new additional address it may be necessary to also create its golden record parents - legal entity and site.
If a site or legal entity parent have to be created, the dummy service uses the additional address values for the legal and site main address.
Likewise, if a legal entity parent has to be created for a site the legal address information is taken from the site main address.

- Confidence Criteria: The dummy golden record service fills all confidence criteria with static dummy values.


#### High-Level Input Output Examples

```mermaid
---
title: Share Unknown Business Partner Data
---
classDiagram
  direction LR
  class Input{
    ExternalId: Own-ID
    Name Parts: [Company AG, Main Office]
    Physical Country: DE
    Physical City: Berlin
    Physical Postcode: 12435
    Street Name: Main Street
    Street Number: 123
  }

  class Output{
    ExternalId: Own-ID
    Legal Entity Bpn: BPNL000000000001
    Address Bpn: BPNA000000000001
    Name Parts: [Company AG, Main Office]
    Legal Name: Company AG Main Office
    Address Type: Legal Address
    Physical Country: DE
    Physical City: Berlin
    Physical Postcode: 12435
    Street Name: Main Street
    Street Number: 123
    Legal Entity Confidence: ...
    Site Confidence: ...
    Address Confidence: ...
  }

  Input --> Output
```

```mermaid
---
title: Share Legal Entity
---
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Name: Company AG
         Address Type: Legal Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
   class Output{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Address Bpn: BPNA000000000001
         Legal Name: Company AG
         Address Type: Legal Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         Legal Entity Confidence: ...
         Address Confidence: ...
   }

    Input --> Output
```

```mermaid
---
title: Share Site
---
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Site Name: Main Site
         Address Type: Site Main Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         IsOwnCompanyData: true
    }
   
   class Output{
         ExternalId: Own-ID
         Legal Name: Company AG
         Legal Entity Bpn: BPNL000000000001
         Site Bpn: BPNS000000000001
         Address Bpn: BPNA000000000001
         Address Type: Site Main Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         Legal Entity Confidence: ...
         Site Confidence: ...
         Address Confidence: ...
         IsOwnCompanyData: true
   }

    Input --> Output
``` 
```mermaid
---
title: Share Additional Address of Legal Entity
---
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
   class Output{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Address Bpn: BPNA000000000001
         Legal Name: Company AG
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         Legal Entity Confidence: ...
         Address Confidence: ...
   }

    Input --> Output
```

```mermaid
---
title: Share Additional Address of Site
---
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Site Bpn: BPNS000000000001
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         IsOwnCompanyData: true
    }
   
   class Output{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Site Bpn: BPNS000000000001
         Address Bpn: BPNA000000000001
         Legal Name: Company AG
         Site Name: Main Site
        Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         Legal Entity Confidence: ...
         Site Confidence: ...
         Address Confidence: ...
         IsOwnCompanyData: true
   }

    Input --> Output
```

#### High Level Golden Record Examples

```mermaid
---
title: Share Unknown Business Partner
---
classDiagram
 direction LR

    class Input{
        ExternalId: Own-ID
        Name Parts: [Company AG, Main Office]
        Physical Country: DE
        Physical City: Berlin
        Physical Postcode: 12435
        Street Name: Main Street
        Street Number: 123
    }
   
    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
        Legal Name: Company AG Main Office
        Legal Address Physical Country: DE
        Legal Address Physical City: Berlin
        Legal Address Physical Postcode: 12435
        Legal Address Street Name: Main Street
        Legal Address Street Number: 123
        Legal Entity Confidence: ...
        Legal Address Confidence: ...
    }

    Input --> LegalEntity
```

```mermaid
---
title: Share Legal Entity
---
classDiagram
 direction LR

    class Input{
         ExternalId: Own-ID
         Legal Name: Company AG
         Address Type: Legal Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
        Legal Name: Company AG Main Office
        Legal Address Physical Country: DE
        Legal Address Physical City: Berlin
        Legal Address Physical Postcode: 12435
        Legal Address Street Name: Main Street
        Legal Address Street Number: 123
        Legal Entity Confidence: ...
        Legal Address Confidence: ...
    }

    Input --> LegalEntity
```

```mermaid
---
title: Share Site
---
classDiagram
 direction LR

    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Site Name: Main Site
         Address Type: Site Main Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
         IsOwnCompanyData: true
    }
   
    class Site{
        BPNS: BPNS000000000001
        Site Name: Main Site
        Main Address Physical Country: DE
        Main Address Physical City: Berlin
        Main Address Physical Postcode: 12435
        Main Address Street Name: Main Street
        Main Address Street Number: 123
        Site Confidence: ...
        Main Address Confidence: ...
    }

    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
    }

    Input --> Site
    Site --> LegalEntity: belongs to
```


```mermaid
---
title: Share Additional Address Of Site
---
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Site Bpn: BPNS000000000001
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
    class AdditionalAddress["Additional Address"]{
        BPNA: BPNA000000000001
        Name: Gate 1
        Physical Country: DE
        Physical City: Berlin
        Physical Postcode: 12435
        Street Name: Main Street
        Street Number: 123
        Confidence: ...
    }

    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
    }

    class Site{
        BPNS: BPNS000000000001
    }

    Input --> AdditionalAddress
    AdditionalAddress --> Site: belongs to
    Site --> LegalEntity: belongs to
```

```mermaid
---
title: Share Additional Address Of Legal Entity
---
classDiagram
 direction LR

    class Input{
         ExternalId: Own-ID
         Legal Entity Bpn: BPNL000000000001
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
    class AdditionalAddress["Additional Address"]{
        BPNA: BPNA000000000001
        Name: Gate 1
        Physical Country: DE
        Physical City: Berlin
        Physical Postcode: 12435
        Street Name: Main Street
        Street Number: 123
        Confidence: ...
    }

    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
    }

    Input --> AdditionalAddress
    AdditionalAddress --> LegalEntity: belongs to
```

```mermaid
classDiagram
  direction LR
    class Input{
         ExternalId: Own-ID
         Legal Entity Name: Company AG
         Site Name: Main Site
         Address Name: Gate 1
         Address Type: Additional Address
         Physical Country: DE
         Physical City: Berlin
         Physical Postcode: 12435
         Street Name: Main Street
         Street Number: 123
    }
   
    class AdditionalAddress["Additional Address"]{
        BPNA: BPNA000000000001
        Name: Gate 1
        Physical Country: DE
        Physical City: Berlin
        Physical Postcode: 12435
        Street Name: Main Street
        Street Number: 123
        Confidence: ...
    }

    class LegalEntity["Legal Entity"]{
        BPNL: BPNL000000000001
        Legal Name: Company AG Main Office
        Legal Address Physical Country: DE
        Legal Address Physical City: Berlin
        Legal Address Physical Postcode: 12435
        Legal Address Street Name: Main Street
        Legal Address Street Number: 123
        Legal Entity Confidence: ...
        Legal Address Confidence: ...
    }

    class Site{
        BPNS: BPNS000000000001
        Site Name: Main Site
        Main Address Physical Country: DE
        Main Address Physical City: Berlin
        Main Address Physical Postcode: 12435
        Main Address Street Name: Main Street
        Main Address Street Number: 123
        Site Confidence: ...
        Main Address Confidence: ...
    }

    Input --> AdditionalAddress
    AdditionalAddress --> Site: belongs to
    Site --> LegalEntity: belongs to
```

## Orchestrator API

This API offers endpoints for retrieving and resolving business partner data being processed inside the golden record process.

#### Tasks

Business partner data to be processed come in processing tasks with their own task ID.
A processing service receives the business partner along with the task ID.
The service than can process the data and post the result of the task back to the API with the matching task ID.


#### Processing Steps

Business partner in the golden record process goes through different processing steps.
A golden record processing service can query and post results for the processing step it is responsible for.

#### Clean And Sync Step

Currently, there is only one step supported: CleanAndSync.
In this step the whole business partner process - including duplication check, natural person screening and cleaning of data - should be conducted.

The business partner that this step receives is the data the sharing member provided.
Depending on what the sharing member provided the data could be completely uncategorized or pre-categorized.
This data needs to be verified and corrected.
If that is not possible the data can be returned to the golden record process with an error message.

As a result for this step the golden record process expects the following:

1. If the business partner data refers to existing golden records the BPNs should be provided in the BPN reference fields.
2. The whole golden record hierarchy should be provided.
That means if the business partner data contains an additional address, the result should also contain the possible site parent and the legal entity parent information


### Mapping of Gate to Orchestrator Business Partner Data

```mermaid
---
title: Share Unknown Business Partner Data Without Site
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: NULL
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
      Site: NULL
      Additional Address: NULL
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: F
  }
  
  class LegalEntity{
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: EMPTY
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
```

```mermaid
---
title: Share Legal Address Without Site
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: NULL
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
      Site: NULL
      Additional Address: NULL
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: NULL
  }
  
  class LegalEntity{
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: F
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
```

```mermaid
---
title: Share Site Main Address
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: E
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
      Additional Address: NULL
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: NULL
  }
  
  class LegalEntity{
      BPN Reference: D
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: NULL
  }
  
  class Site{
      BPN Reference: E
      Site Name: E
      Identifiers: E
      States: E
      Site Main Address: F
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
  BusinessPartner --> Site: E
```

```mermaid
---
title: Share Legal And Site Main Address
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: E
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
      Additional Address: NULL
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: NULL
  }
  
  class LegalEntity{
      BPN Reference: D
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: F
  }
  
  class Site{
      BPN Reference: E
      Site Name: E
      Identifiers: EMPTY
      States: E
      Site Main Address: NULL
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
  BusinessPartner --> Site: E
```

```mermaid
---
title: Share Additional Address Of Site
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: E
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: NULL
  }
  
  class LegalEntity{
      BPN Reference: D
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: EMPTY
  }
  
  class Site{
      BPN Reference: E
      Site Name: E
      Identifiers: EMPTY
      States: E
      Site Main Address: NULL
  }
  
  class AdditionalAddress{
      BPN Reference: F
      Identifiers: EMPTY
      States: F
      Physical Address: F
      Alternative Address: F
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
  BusinessPartner --> Site: E
  BusinessPartner --> AdditionalAddress: F
```

```mermaid
---
title: Share Additional Address Of Legal Entity
---
classDiagram
  direction LR
  class Input["Gate Input"]{
    Name Parts: A
    Identifiers: B
    States: C
    Legal Entity Properties: D
    Site Properties: NULL
    Address Properties: F
    Is Own Company Data: G
  }

  class BusinessPartner["Business Partner"]{
      Name Parts: EMPTY
      Owning Company: G
      Site: NULL
  }
  
  class Uncategorized{
      Name Parts: A
      Identifiers: B
      States: C
      Address: NULL
  }
  
  class LegalEntity{
      BPN Reference: D
      Legal Name: D
      Legal Short Name: D
      Legalform: D
      Identifiers: EMPTY
      States: D
      Legal Address: EMPTY
  }
  
  class AdditionalAddress{
      BPN Reference: F
      Identifiers: EMPTY
      States: F
      Physical Address: F
      Alternative Address: F
  }

  Input ..> BusinessPartner
  BusinessPartner --> Uncategorized
  BusinessPartner --> LegalEntity: D
  BusinessPartner --> AdditionalAddress: F
```

## Access BPDM over EDC

This section details how a sharing member EDC can access an EDC exposing the BPDM API as assets. Before you can access the assets make sure that the BPDM EDC
has been configured to provide assets for your company's BPN (see [Operator View](OPERATOR_VIEW.md)).
This [POSTMAN collection](../postman/EDC BPDM Consumer.postman_collection.json) gives example requests for communicating with a deployed BPDM EDC over a sharing
member EDC.
In the collection you will see how you can negotiate for a BPDM data offer.
Offers are separated into purposes (defined in the BPDM framework agreement) on why you want to access the BPDM API.
First, you need to select the offer based on your purpose.
Afterward you can negotiate for a contract agreement in order to get access to the data.
The final result of that negotiation will be a transfer token with which you can navigate the BPDM APIs over the BPDM EDC's public API (which acts as a proxy).


## NOTICE

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

- SPDX-License-Identifier: Apache-2.0
- SPDX-FileCopyrightText: 2023,2024 ZF Friedrichshafen AG
- SPDX-FileCopyrightText: 2023,2024 SAP SE
- SPDX-FileCopyrightText: 2023,2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
- SPDX-FileCopyrightText: 2023,2024 Mercedes Benz Group
- SPDX-FileCopyrightText: 2023,2024 Robert Bosch GmbH
- SPDX-FileCopyrightText: 2023,2024 Schaeffler AG
- SPDX-FileCopyrightText: 2023,2024 Contributors to the Eclipse Foundation
- Source URL: https://github.com/eclipse-tractusx/bpdm